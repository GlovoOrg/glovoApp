package com.api.glovoCRM.Services.AuthServices;

import com.api.glovoCRM.Repositories.UserDAOs.RoleRepository;
import com.api.glovoCRM.Repositories.UserDAOs.UserRepository;
import com.api.glovoCRM.Models.UserModels.Role;
import com.api.glovoCRM.Models.UserModels.User;
import com.api.glovoCRM.constants.ERoles;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;

@Component
public class AuthService {


    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public AuthService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public void assignDefaultRole(User user) {
        Role role = roleRepository.findByName(ERoles.ROLE_CUSTOMER)
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName(ERoles.ROLE_CUSTOMER);
                    return roleRepository.saveAndFlush(newRole);
                });
        user.setRoles(new HashSet<>(Collections.singletonList(role)));
        userRepository.save(user);
    }
    public void assignDefaultRoleStuff(User user, ERoles TypeRole) {
        Role role = roleRepository.findByName(TypeRole).orElseGet(
                ()-> {
                    Role newRole = new Role();
                    newRole.setName(TypeRole);
                    return roleRepository.saveAndFlush(newRole);
                });
        user.setRoles(new HashSet<>(Collections.singletonList(role)));
        userRepository.save(user);
    }
}
