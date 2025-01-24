package com.api.glovoCRM.DAOs.UserDAOs;

import com.api.glovoCRM.Models.UserModels.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserDAO extends JpaRepository<User, Long> {
    List<User> findByLastLoginDateBefore(LocalDateTime date);
}
