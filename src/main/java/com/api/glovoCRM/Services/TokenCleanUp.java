package com.api.glovoCRM.Services;

import com.api.glovoCRM.DAOs.RefreshTokenDAO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
@Slf4j
public class TokenCleanUp {

    private final RefreshTokenDAO refreshTokenDAO;

    @Autowired
    public TokenCleanUp(RefreshTokenDAO refreshTokenDAO) {
        this.refreshTokenDAO = refreshTokenDAO;
    }

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void cleanupExpiredTokens() {
        log.info("Запуск очистки протухших токенов...");
        refreshTokenDAO.deleteByExpiryDateBefore(Instant.now());
        log.info("Очистка завершена.");
    }
}
