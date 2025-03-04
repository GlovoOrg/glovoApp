package com.api.glovoCRM.Services.AuthServices.Mail;

import com.api.glovoCRM.Repositories.UserRepositories.UserRepository;
import com.api.glovoCRM.Exceptions.AuthExceptions.InvalidCredentialsEx;
import com.api.glovoCRM.Exceptions.BaseExceptions.SuchResourceNotFoundEx;
import com.api.glovoCRM.Models.UserModels.User;
import com.api.glovoCRM.Rest.Requests.AuthRequests.LoginRequestMail;
import com.api.glovoCRM.Rest.Responses.Auth.LoginResponse;
import com.api.glovoCRM.Security.AuthenticationTokens.EmailAuthenticationToken;
import com.api.glovoCRM.Services.AuthServices.TokenService;
import com.api.glovoCRM.Services.AuthServices.VerificationCodeService;
import com.api.glovoCRM.constants.EUserStatuses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@Slf4j
public class LoginServiceMail {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final VerificationCodeService verificationCodeService;
    private final EmailService emailService;

    @Autowired
    public LoginServiceMail(UserRepository userRepository, AuthenticationManager authenticationManager, TokenService tokenService, VerificationCodeService verificationCodeService, EmailService emailService) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.verificationCodeService = verificationCodeService;
        this.emailService = emailService;
    }

    public void TipaLogin(LoginRequestMail request) {
        String email = request.getEmail();
        String password = request.getPassword();
        authenticationManager.authenticate(
                new EmailAuthenticationToken(email, password)
        );

    }
    @Transactional()
    public LoginResponse verifyCode(String email, String code) {
        if (!verificationCodeService.validateAuthCode(email, code)) {
            log.warn("Неверный код для почты: {}", email);
            throw new InvalidCredentialsEx("Неверный код подтверждения");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new SuchResourceNotFoundEx("Пользователь не найден"));
        user.setStatus(EUserStatuses.ACTIVE);
        user.setLastLoginDate(LocalDateTime.now());
        userRepository.save(user);
        EmailAuthenticationToken authenticatedToken = EmailAuthenticationToken.postAuthenticated(
                user.getEmail(),
                null,
                user.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authenticatedToken);
        Map<String, String> tokens = tokenService.generateTokens(user);
        return LoginResponse.builder()
                .accessToken(tokens.get("access_token"))
                .refreshToken(tokens.get("refresh_token"))
                .build();
    }
    public void resendVerificationCode(String email) {
        if (!userRepository.existsByEmail(email)) {
            throw new UsernameNotFoundException("Пользователь не найден");
        }

        emailService.resendAuthCode(email);
        log.info("Повторная отправка кода для: {}", email);
    }
}
