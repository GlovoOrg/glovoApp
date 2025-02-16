package com.api.glovoCRM.Services;

import com.api.glovoCRM.DAOs.UserDAOs.UserDAO;
import com.api.glovoCRM.constants.EUserStatuses;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DeleteNotVerifiedUsers {

    private final UserDAO userDAO;

    @Autowired
    public DeleteNotVerifiedUsers(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Scheduled(cron = "0 0 0 */3 * *") // Каждые 3 дня в 00:00
    @Transactional
    public void deleteUnverifiedUsers() {
        log.info("Запуск очистки неподтвержденных пользователей...");
        int deletedCount = userDAO.deleteByStatus(EUserStatuses.PENDING_EMAIL_VERIFICATION);
        log.info("Удалено {} пользователей с неподтвержденным email", deletedCount);
    }
}
