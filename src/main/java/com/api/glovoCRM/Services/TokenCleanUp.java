package com.api.glovoCRM.Services;

import com.api.glovoCRM.Repositories.RefreshTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
@Slf4j
public class TokenCleanUp {

    private final RefreshTokenRepository refreshTokenRepository;

    @Autowired
    public TokenCleanUp(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void cleanupExpiredTokens() {
        log.info("Запуск очистки протухших токенов...");
        refreshTokenRepository.deleteByExpiryDateBefore(Instant.now());
        log.info("Очистка завершена.");
    }
}
