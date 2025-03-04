package com.api.glovoCRM.Services.AuthServices.Stuff;

import com.api.glovoCRM.Repositories.UserRepositories.UserRepository;
import com.api.glovoCRM.Exceptions.AuthExceptions.UserNotVerifiedEx;
import com.api.glovoCRM.Models.UserModels.User;
import com.api.glovoCRM.Rest.Responses.Auth.LoginResponse;
import com.api.glovoCRM.Security.AuthenticationTokens.PostAuthenticationTokenStuff;
import com.api.glovoCRM.Security.AuthenticationTokens.PreAuthenticationTokenStuff;
import com.api.glovoCRM.Services.AuthServices.VerificationCodeService;
import com.api.glovoCRM.Services.AuthServices.TokenService;
import com.api.glovoCRM.constants.EUserStatuses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class StuffLoginService {
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final VerificationCodeService verificationCodeService;
    private final UserRepository userRepository;

    @Autowired
    public StuffLoginService(AuthenticationManager authenticationManager, TokenService tokenService, VerificationCodeService verificationCodeService, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.verificationCodeService = verificationCodeService;
        this.userRepository = userRepository;
    }

    public String TipaLoginStuff(String login, String password) {
        Authentication preAuth = authenticationManager.authenticate(
                new PreAuthenticationTokenStuff(login, password)
        );
        SecurityContextHolder.getContext().setAuthentication(preAuth);
        return "Код подтверждения отправлен";
    }
    public LoginResponse finalochkaLogin(String login, String code) {
        if (!verificationCodeService.validateAuthCode(login, code)) {
            throw new UserNotVerifiedEx("Неверный код подтверждения");
        }

        Authentication postAuth = authenticationManager.authenticate(
                new PostAuthenticationTokenStuff(login, null)
        );
        User user = (User) postAuth.getPrincipal();
        user.setStatus(EUserStatuses.ACTIVE);
        userRepository.save(user);
        Map<String, String> mapOfTokens = tokenService.generateTokens(user);
        return new LoginResponse(mapOfTokens.get("access_token"), mapOfTokens.get("refresh_token"));
    }
}
