package com.api.glovoCRM.Services;

import com.api.glovoCRM.DAOs.UserDAOs.UserDAO;
import com.api.glovoCRM.Exceptions.BaseExceptions.SuchResourceNotFoundEx;
import com.api.glovoCRM.Models.UserModels.User;
import com.api.glovoCRM.Rest.Requests.UserRequests.AdminPatchRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public abstract class BaseUserService {
    private final UserDAO userDAO;

    protected BaseUserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }


    protected User getUserFromSecurityContext() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userDAO.findByName(username)
                .orElseThrow(() -> new SuchResourceNotFoundEx("User not found"));
    }

    protected User findUserById(Long id) {
        return userDAO.findById(id)
                .orElseThrow(() -> new SuchResourceNotFoundEx("User not found"));
    }
}
