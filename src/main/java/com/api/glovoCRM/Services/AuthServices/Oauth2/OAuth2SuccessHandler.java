package com.api.glovoCRM.Services.AuthServices.Oauth2;

import com.api.glovoCRM.DAOs.UserDAOs.UserDAO;
import com.api.glovoCRM.Models.UserModels.User;
import com.api.glovoCRM.Rest.Responses.Auth.LoginResponse;
import com.api.glovoCRM.Rest.Responses.Auth.oauth2Response;
import com.api.glovoCRM.Services.AuthServices.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final CustomOauth2UserService customOauth2UserService;
    private final TokenService tokenService;
    private final UserDAO userDAO;

    @Autowired
    public OAuth2SuccessHandler(CustomOauth2UserService customOauth2UserService, TokenService tokenService, UserDAO userDAO) {
        this.customOauth2UserService = customOauth2UserService;
        this.tokenService = tokenService;
        this.userDAO = userDAO;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (authentication == null || !(authentication.getPrincipal() instanceof OAuth2User)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        User user = userDAO.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        oauth2Response oauth2Response;
        if ("Пользователь успешно создан".equals(oAuth2User.getAttribute("message"))) {
            oauth2Response = new oauth2Response("Пользователь успешно создан");
            response.setStatus(HttpServletResponse.SC_CREATED);
        } else if("Пользователь успешно привязал социальный аккаунт к существующему аккаунту Glovo"
                .equals(oAuth2User.getAttribute("message"))) {
            Map<String, String> tokens = tokenService.generateTokens(user);
            oauth2Response = new oauth2Response(
                    tokens.get("access_token").toString(),
                    tokens.get("refresh_token").toString(),
                    "Поздравляю вы успешно привязали социальный аккаунт к вашему существующему аккаунту Glovo"
                    );
            response.setStatus(HttpServletResponse.SC_OK);
        }else {
        Map<String, String> map = tokenService.generateTokens(user);
        oauth2Response = new oauth2Response(
                map.get("access_token").toString(),
                map.get("refresh_token").toString()
        );
        response.setStatus(HttpServletResponse.SC_OK);
        }
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(oauth2Response));
    }
}