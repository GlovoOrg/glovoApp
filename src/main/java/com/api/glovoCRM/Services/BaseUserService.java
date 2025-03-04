package com.api.glovoCRM.Services;

import com.api.glovoCRM.Repositories.UserRepositories.UserRepository;
import com.api.glovoCRM.Exceptions.BaseExceptions.SuchResourceNotFoundEx;
import com.api.glovoCRM.Models.UserModels.User;
import com.api.glovoCRM.Security.AuthenticationTokens.PhoneAuthenticationToken;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
@Slf4j
@Component
public abstract class BaseUserService {
    private final UserRepository userRepository;

    protected BaseUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    protected User getUserFromSecurityContext() {
        String identifier = getUserByIdentificator(SecurityContextHolder.getContext().getAuthentication());

        log.info("Ищем пользователя по идентификатору: {}", identifier);

        return userRepository.findByName(identifier)
                .or(() -> userRepository.findByEmail(identifier))
                .or(() -> userRepository.findByPhoneNumber(identifier))
                .or(() -> userRepository.findByLogin(identifier))
                .orElseThrow(() -> new SuchResourceNotFoundEx("User not found"));
    }

    protected static String getUserByIdentificator(Authentication authentication) {
        Object principal = authentication.getPrincipal();

        if (principal instanceof OAuth2User oAuth2User) {
            return oAuth2User.getAttribute("email");
        }
        if (principal instanceof PhoneAuthenticationToken phoneAuth) {
            return phoneAuth.getPrincipal().toString();
        }
        if (principal instanceof String usernameOrPhone) {
            return usernameOrPhone;
        }
        throw new SuchResourceNotFoundEx("Unsupported principal type: " + principal.getClass().getName());
    }


    protected User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new SuchResourceNotFoundEx("User not found"));
    }
}
