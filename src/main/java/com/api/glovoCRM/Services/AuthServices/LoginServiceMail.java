package com.api.glovoCRM.Services.AuthServices;

import com.api.glovoCRM.DAOs.UserDAOs.UserDAO;
import com.api.glovoCRM.Exceptions.AuthExceptions.InvalidCredentialsEx;
import com.api.glovoCRM.Exceptions.BaseExceptions.SuchResourceNotFoundEx;
import com.api.glovoCRM.Models.UserModels.User;
import com.api.glovoCRM.Rest.Requests.AuthRequests.LoginRequestMail;
import com.api.glovoCRM.Rest.Responses.Auth.LoginResponse;
import com.api.glovoCRM.Security.AuthenticationTokens.EmailAuthenticationToken;
import com.api.glovoCRM.Security.jwt.JwtCore;
import com.api.glovoCRM.Services.AuthServices.Mail.EmailService;
import com.api.glovoCRM.Services.AuthServices.Mail.VerificationCodeService;
import com.api.glovoCRM.Utils.Cache.BlackListService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Slf4j
public class LoginServiceMail {
    private final UserDAO userDAO;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final VerificationCodeService verificationCodeService;

    @Autowired
    public LoginServiceMail( UserDAO userDAO, AuthenticationManager authenticationManager, TokenService tokenService, VerificationCodeService verificationCodeService) {
        this.userDAO = userDAO;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.verificationCodeService = verificationCodeService;
    }

    public void TipaLogin(LoginRequestMail request) {
        String email = request.getEmail();
        String password = request.getPassword();
        authenticationManager.authenticate(
                new EmailAuthenticationToken(email, password)
        );

    }
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public LoginResponse verifyCode(String email, String code) {
        if (!verificationCodeService.validateAuthCode(email, code)) {
            log.warn("Неверный код для почты: {}", email);
            throw new InvalidCredentialsEx("Неверный код подтверждения");
        }

        User user = userDAO.findByEmail(email)
                .orElseThrow(() -> new SuchResourceNotFoundEx("Пользователь не найден"));

        Map<String, String> tokens = tokenService.generateTokens(user);
        return LoginResponse.builder()
                .accessToken(tokens.get("access_token"))
                .refreshToken(tokens.get("refresh_token"))
                .build();
    }

}
