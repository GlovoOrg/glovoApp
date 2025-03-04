package com.api.glovoCRM.Repositories.UserRepositories;

import com.api.glovoCRM.Models.UserModels.Role;
import com.api.glovoCRM.constants.ERoles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERoles eRoles);
}
