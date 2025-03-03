package com.api.glovoCRM.Services.AuthServices.Phone;

import com.api.glovoCRM.Repositories.UserDAOs.UserRepository;
import com.api.glovoCRM.Exceptions.AuthExceptions.InvalidCredentialsEx;
import com.api.glovoCRM.Exceptions.BaseExceptions.SuchResourceNotFoundEx;
import com.api.glovoCRM.Models.UserModels.User;
import com.api.glovoCRM.Rest.Requests.AuthRequests.LoginRequestPhone;
import com.api.glovoCRM.Rest.Responses.Auth.LoginResponse;
import com.api.glovoCRM.Security.AuthenticationTokens.PhoneAuthenticationToken;
import com.api.glovoCRM.Services.AuthServices.VerificationCodeService;
import com.api.glovoCRM.Services.AuthServices.TokenService;
import com.api.glovoCRM.constants.EUserStatuses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Slf4j
public class LoginPhoneService {
    private final AuthenticationManager authenticationManager;
    private final VerificationCodeService verificationCodeService;
    private final TokenService tokenService;
    private final UserRepository userRepository;

    public LoginPhoneService(AuthenticationManager authenticationManager, VerificationCodeService verificationCodeService, TokenService tokenService, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.verificationCodeService = verificationCodeService;
        this.tokenService = tokenService;
        this.userRepository = userRepository;
    }

    public void TipaLoginPhone(LoginRequestPhone request){
        String phoneNumber = request.getPhoneNumber();
        String password = request.getPassword();
        log.info("Создание PhoneAuthenticationToken с номером: {} и паролем: {}", phoneNumber, password);

        authenticationManager.authenticate(new PhoneAuthenticationToken(phoneNumber, password));

    }
    @Transactional()
    public LoginResponse verifyCode(String phoneNumber, String code){
        if (!verificationCodeService.validateAuthCode(phoneNumber, code)) {
            log.warn("Неверный код для номера: {}", phoneNumber);
            throw new InvalidCredentialsEx("Неверный код подтверждения");
        }
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new SuchResourceNotFoundEx("Пользователь не найден"));
        user.setStatus(EUserStatuses.ACTIVE);
        userRepository.save(user);
        Map<String, String> tokens = tokenService.generateTokens(user);
        PhoneAuthenticationToken authenticatedToken = new PhoneAuthenticationToken(
                user.getPhoneNumber(),
                null,
                user.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authenticatedToken);

        return LoginResponse.builder()
                .accessToken(tokens.get("access_token"))
                .refreshToken(tokens.get("refresh_token"))
                .build();
    }
}
