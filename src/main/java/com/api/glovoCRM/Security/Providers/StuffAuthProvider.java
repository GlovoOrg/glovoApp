package com.api.glovoCRM.Security.Providers;

import com.api.glovoCRM.Repositories.UserRepositories.UserRepository;
import com.api.glovoCRM.Exceptions.AuthExceptions.InvalidCredentialsEx;
import com.api.glovoCRM.Exceptions.AuthExceptions.UserNotVerifiedEx;
import com.api.glovoCRM.Models.UserModels.User;
import com.api.glovoCRM.Security.AuthenticationTokens.PostAuthenticationTokenStuff;
import com.api.glovoCRM.Security.AuthenticationTokens.PreAuthenticationTokenStuff;
import com.api.glovoCRM.Services.AuthServices.VerificationCodeService;
import com.api.glovoCRM.TelegramCodeSender.TelegramNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class StuffAuthProvider implements AuthenticationProvider {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TelegramNotificationService telegramNotificationService;
    private final VerificationCodeService verificationCodeService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (authentication instanceof PreAuthenticationTokenStuff) {
            String login = (String) authentication.getPrincipal();
            log.info("Первый этап аутентификации для пользователя: {}", login);
            String password = (String) authentication.getCredentials();
            User user = userRepository.findByLogin(login)
                    .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
            if (!passwordEncoder.matches(password, user.getPassword())) {
                throw new InvalidCredentialsEx("Неверный пароль");
            }
            try {
                String code = verificationCodeService.generateCode();
                verificationCodeService.saveAuthCode(login, code);
                telegramNotificationService.sendLoginCode(login, code);
            } catch (Exception e) {
                log.error("Ошибка при отправке кода: {}", e.getMessage());
                throw new AuthenticationServiceException("Ошибка аутентификации");
            }

            PreAuthenticationTokenStuff token = new PreAuthenticationTokenStuff(login, null);
            token.setAuthenticated(false);
            return token;

        } else if (authentication instanceof PostAuthenticationTokenStuff) {
            String login = (String) authentication.getPrincipal();
            log.info("Второй этап аутентификации для пользователя: {}", login);
            if (!verificationCodeService.isCodeConfirmed(login)) {
                throw new UserNotVerifiedEx("Код подтверждения не подтвержден");
            }

            User user = userRepository.findByLogin(login)
                    .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
            PostAuthenticationTokenStuff token = new PostAuthenticationTokenStuff(user, user.getAuthorities());
            token.setAuthenticated(true);
            return token;
        } else {
            throw new IllegalArgumentException("Неизвестный тип токена");
        }
    }
    @Override
    public boolean supports(Class<?> authentication) {
        return PreAuthenticationTokenStuff.class.isAssignableFrom(authentication) ||
                PostAuthenticationTokenStuff.class.isAssignableFrom(authentication);
    }
}
