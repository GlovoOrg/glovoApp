package com.api.glovoCRM.Services.AuthServices;

import com.api.glovoCRM.DAOs.UserDAOs.RoleDAO;
import com.api.glovoCRM.DAOs.UserDAOs.UserDAO;
import com.api.glovoCRM.Models.UserModels.Role;
import com.api.glovoCRM.Models.UserModels.User;
import com.api.glovoCRM.constants.ERoles;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;

@Component
public class AuthService {


    private final UserDAO userDAO;
    private final RoleDAO roleDAO;

    public AuthService(UserDAO userDAO, RoleDAO roleDAO) {
        this.userDAO = userDAO;
        this.roleDAO = roleDAO;
    }

    public void assignDefaultRole(User user) {
        Role role = roleDAO.findByName(ERoles.ROLE_CUSTOMER)
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName(ERoles.ROLE_CUSTOMER);
                    return roleDAO.saveAndFlush(newRole);
                });
        user.setRoles(new HashSet<>(Collections.singletonList(role)));
        userDAO.save(user);
    }
    public void assignDefaultRoleStuff(User user, ERoles TypeRole) {
        Role role = roleDAO.findByName(TypeRole).orElseGet(
                ()-> {
                    Role newRole = new Role();
                    newRole.setName(TypeRole);
                    return roleDAO.saveAndFlush(newRole);
                });
        user.setRoles(new HashSet<>(Collections.singletonList(role)));
        userDAO.save(user);
    }
}
