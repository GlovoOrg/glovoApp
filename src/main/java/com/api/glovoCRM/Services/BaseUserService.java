package com.api.glovoCRM.Services;

import com.api.glovoCRM.Repositories.UserDAOs.UserRepository;
import com.api.glovoCRM.Exceptions.BaseExceptions.SuchResourceNotFoundEx;
import com.api.glovoCRM.Models.UserModels.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public abstract class BaseUserService {
    private final UserRepository userRepository;

    protected BaseUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    protected User getUserFromSecurityContext() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByName(username)
                .orElseThrow(() -> new SuchResourceNotFoundEx("User not found"));
    }

    protected User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new SuchResourceNotFoundEx("User not found"));
    }
}
