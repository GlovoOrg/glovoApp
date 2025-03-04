package com.api.glovoCRM.Repositories.UserRepositories;

import com.api.glovoCRM.Models.UserModels.User;
import com.api.glovoCRM.constants.EUserStatuses;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
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

    @Transactional
    @Modifying
    @CacheEvict (value = "telegramChatIds", key = "#login")
    @Query("UPDATE User u SET u.chatId = :chatId WHERE u.login = :login")
    void updateTelegramChatId(@Param("login") String login, @Param("chatId") String chatId);

    @Modifying
    @Query("UPDATE User u SET u.chatId= NULL WHERE u.chatId = :chatId")
    void clearTelegramChatId(@Param("chatId") String chatId);

    @Query("SELECT u.chatId FROM User u WHERE u.login = :login")
    Optional<String> findChatIdByLogin(@Param("login") String login);

    Optional<User> findByPhoneNumber(String phoneNumber);

    boolean existsByPhoneNumber(String phoneNumber);

    Optional<User> findByLogin(String login);

    Optional<User> findByChatId(String chatId);

    boolean existsByLogin(String login);
}
