package com.api.glovoCRM.DAOs.UserDAOs;

import com.api.glovoCRM.Models.UserModels.User;
import com.api.glovoCRM.constants.AuthProviders;
import com.api.glovoCRM.constants.EUserStatuses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserDAO extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    List<User> findByLastLoginDateBefore(LocalDateTime date);

    Optional<User> findByName(String name);

    Optional<User> findByEmail(String email);

    boolean existsByName(String name);

    boolean existsByEmail(String email);

    @Modifying
    @Query("DELETE FROM User u WHERE u.status = :status")
    int deleteByStatus(@Param("status") EUserStatuses status);

    @Query("SELECT u FROM User u JOIN u.socialAccounts sa WHERE sa.providerId = :providerId")
    Optional<User> findBySocialAccountsProviderId(@Param("providerId") String providerId);

    Optional<User> findByPhoneNumber(String phoneNumber);

    boolean existsByPhoneNumber(String phoneNumber);


}
