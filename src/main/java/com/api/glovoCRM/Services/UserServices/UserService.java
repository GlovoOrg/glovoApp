package com.api.glovoCRM.Services.UserServices;

import com.api.glovoCRM.Repositories.UserRepositories.UserRepository;
import com.api.glovoCRM.Models.UserModels.User;
import com.api.glovoCRM.constants.EUserStatuses;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    @Scheduled(cron = "0 0 0 * * ?", zone = "Europe/Moscow")
    public void markInactiveUsers() {
        log.info("Запуск задачи markInactiveUsers... Загрузка");
        LocalDateTime threeMonthsBefore = LocalDateTime.now().minusMonths(3);
        List<User> inactiveUsers = userRepository.findByLastLoginDateBefore(threeMonthsBefore);
        log.info("Найдено неактивных пользователей в БД: {}", inactiveUsers.size());
        inactiveUsers.forEach(user -> user.setStatus(EUserStatuses.INACTIVE));
        userRepository.saveAll(inactiveUsers);
        log.info("Завершено выполнение задачи markInactiveUsers.");
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

}
