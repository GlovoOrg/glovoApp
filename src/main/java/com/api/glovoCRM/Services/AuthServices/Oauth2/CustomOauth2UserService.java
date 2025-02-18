package com.api.glovoCRM.Services.AuthServices.Oauth2;

import com.api.glovoCRM.DAOs.UserDAOs.UserDAO;
import com.api.glovoCRM.Models.UserModels.SocialAccount;
import com.api.glovoCRM.Models.UserModels.User;
import com.api.glovoCRM.Services.AuthServices.TokenService;
import com.api.glovoCRM.Services.AuthServices.AuthService;
import com.api.glovoCRM.constants.AuthProviders;
import com.api.glovoCRM.constants.EUserStatuses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import java.util.*;
/* todo вроде как пока все ок
* Для OAuth2 не нужно создавать отдельный провайдер, так как Spring Security предоставляет встроенную поддержку.
Если вам нужно добавить кастомную логику, используйте CustomOAuth2UserService.
* */

@Slf4j
@Service
public class CustomOauth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserDAO userDAO;
    private final AuthService authService;
    private final TokenService tokenService;

    @Autowired
    public CustomOauth2UserService(AuthService authService, UserDAO userDAO, TokenService tokenService) {
        this.userDAO = userDAO;
        this.authService = authService;
        this.tokenService = tokenService;
    }
    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("Начало обработки OAuth2-запроса для провайдера: {}", userRequest.getClientRegistration().getRegistrationId());

        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);
        String provider = userRequest.getClientRegistration().getRegistrationId();
        String email = extractEmail(oAuth2User, provider, userRequest);
        String name = extractName(oAuth2User, provider);

        if (email == null || name == null) {
            throw new OAuth2AuthenticationException(new OAuth2Error("invalid_user_info"), "Почта или имя отсутствуют");
        }

        log.info("Пользователь авторизован через {}: email={}, name={}", provider, email, name);
        //todo можно обхединить в 1 метод в query
        // Ищем пользователя по email или социальному ID
        User user = userDAO.findByEmail(email).orElse(null);
        if (user == null) {
            user = userDAO.findBySocialAccountsProviderId(oAuth2User.getName()).orElse(null);
        }

        if (user == null) {
            return registerUser(oAuth2User, email, name, provider);
        }

        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            authService.assignDefaultRole(user);
        }
        return loginUser(oAuth2User, user, provider);
    }

    private String extractEmail(OAuth2User oAuth2User, String provider, OAuth2UserRequest userRequest) {
        switch (provider.toLowerCase()) {
            case "google", "facebook" -> {
                return oAuth2User.getAttribute("email");
            }
            case "github" -> {
                return (String) Optional.ofNullable(oAuth2User.getAttribute("email"))
                        .orElseGet(() -> fetchEmailFromGitHub(userRequest));
            }
            case "twitter" -> {
                String email = oAuth2User.getAttribute("email");
                return email != null ? email : oAuth2User.getAttribute("name") + "@twitter.com";
            }
            default -> throw new OAuth2AuthenticationException(
                    new OAuth2Error("unsupported_provider"),
                    "Неподдерживаемый провайдер: " + provider
            );
        }
    }
    private String extractName(OAuth2User oAuth2User, String provider) {
        switch (provider.toLowerCase()) {
            case "google", "facebook", "twitter" -> {
                return oAuth2User.getAttribute("name");
            }
            case "github" -> {
                Map<String, Object> attributes = oAuth2User.getAttributes();
                return Optional.ofNullable(attributes.get("name"))
                        .orElseGet(() -> attributes.get("login")).toString();
            }
            default -> throw new OAuth2AuthenticationException(
                    new OAuth2Error("unsupported_provider"),
                    "Неподдерживаемый провайдер: " + provider
            );
        }
    }

    private OAuth2User registerUser(OAuth2User oAuth2User, String email, String name, String provider) {
        log.info("Создание нового пользователя с email={} и name={}", email, name);
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setPassword("");
        user.setStatus(EUserStatuses.PENDING_LOGIN_TO_THE_SYSTEM);

        authService.assignDefaultRole(user);

        SocialAccount socialAccount = new SocialAccount();
        socialAccount.setProvider(AuthProviders.valueOf("AUTH_PROVIDERS_" + provider.toUpperCase()));
        socialAccount.setProviderId(oAuth2User.getName());
        user.addSocialAccount(socialAccount);
        userDAO.save(user);

        Map<String, Object> mapResponse = new HashMap<>(oAuth2User.getAttributes());
        mapResponse.put("message", "Пользователь успешно создан");

        log.info("Пользователь с email={} успешно создан", email);

        return new DefaultOAuth2User(
                user.getAuthorities(),
                mapResponse,
                "email"
        );
    }

    private OAuth2User loginUser(OAuth2User oAuth2User, User user, String provider) {
        log.info("Пользователь с email={} уже существует", user.getEmail());
        boolean hasSocialAccount = user.getSocialAccounts().stream()
                .anyMatch(sa -> sa.getProvider().name().equals("AUTH_PROVIDERS_" + provider.toUpperCase()));
        user.setStatus(EUserStatuses.ACTIVE);
        userDAO.save(user);

        Map<String, Object> mapOfAccessAndRefresh = generateTokens(oAuth2User, user);
        if(!hasSocialAccount && user.getPassword() != null) {
            linkSocialAccount(oAuth2User, user, provider);
            HashMap<String, Object> response = new HashMap<>(oAuth2User.getAttributes());
            response.putAll(Map.of(
                    "message", "Пользователь успешно привязал социальный аккаунт к существующему аккаунту Glovo",
                    "access_token", mapOfAccessAndRefresh.get("access_token"),
                    "refresh_token", mapOfAccessAndRefresh.get("refresh_token")
            ));
            log.info("Добавлен социальный аккаунт провайдера {} для зарегистрированнаго пользователя: {} ", provider, user.getName());
            return new DefaultOAuth2User(
                    user.getAuthorities(),
                    response,
                    "email"
            );
        }

        if (!hasSocialAccount) {
            linkSocialAccount(oAuth2User, user, provider);
            log.info("Добавлен социальный аккаунт провайдера {}", provider);
        }


        log.info("Токены сгенерированы для существующего пользователя: {}", user.getEmail());

        return new DefaultOAuth2User(
                user.getAuthorities(),
                mapOfAccessAndRefresh,
                "email"
        );
    }
    private void linkSocialAccount(OAuth2User oAuth2User,User user, String provider){
        SocialAccount socialAccount = new SocialAccount();
        socialAccount.setProvider(AuthProviders.valueOf("AUTH_PROVIDERS_" + provider.toUpperCase()));
        socialAccount.setProviderId(oAuth2User.getName());
        user.addSocialAccount(socialAccount);
        userDAO.save(user);
    }

    private Map<String, Object> generateTokens(OAuth2User oAuth2User, User user) {
        Map<String, String> tokens = tokenService.generateTokens(user);
        Map<String, Object> mapOfAccessAndRefresh = new HashMap<>(oAuth2User.getAttributes());
        mapOfAccessAndRefresh.put("access_token", tokens.get("access_token"));
        mapOfAccessAndRefresh.put("refresh_token", tokens.get("refresh_token"));
        return  mapOfAccessAndRefresh;
    }
    private String fetchEmailFromGitHub(OAuth2UserRequest userRequest) {
        String accessToken = userRequest.getAccessToken().getTokenValue();
        if (accessToken == null) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("missing_access_token"),
                    "Токен доступа отсутствует"
            );
        }

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    "https://api.github.com/user/emails",
                    HttpMethod.GET,
                    request,
                    new ParameterizedTypeReference<>() {}
            );

            return Objects.requireNonNull(response.getBody()).stream()
                    .filter(emailData ->
                            Boolean.TRUE.equals(emailData.get("primary")) &&
                                    Boolean.TRUE.equals(emailData.get("verified"))
                    )
                    .findFirst()
                    .map(emailData -> (String) emailData.get("email"))
                    .orElseThrow(() -> new OAuth2AuthenticationException(
                            new OAuth2Error("no_primary_email"),
                            "GitHub не предоставил email"
                    ));
        } catch (Exception e) {
            log.error("Ошибка при запросе email через GitHub API: {}", e.getMessage());
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("github_api_error"),
                    "Ошибка API GitHub",
                    e
            );
        }
    }
}

