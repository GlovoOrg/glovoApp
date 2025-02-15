package com.api.glovoCRM.Services.AuthServices.Mail;

import com.api.glovoCRM.Utils.Cache.CacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.UUID;

@Service
@Slf4j
public class VerificationCodeService {
    private static final String CODE_CHARACTERS = "0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final CacheService cacheService;
    private final int codeExpirationSec;
    private final int linkExpirationSec;

    @Autowired
    public VerificationCodeService(CacheService cacheService,
                                   @Value("${auth.code.expiration}") int codeExpirationSec,
                                   @Value("${auth.link.expiration}") int linkExpirationSec) {
        this.cacheService = cacheService;
        this.codeExpirationSec = codeExpirationSec;
        this.linkExpirationSec = linkExpirationSec;
    }

    public String generateCode() {
        StringBuilder code = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            code.append(CODE_CHARACTERS.charAt(RANDOM.nextInt(CODE_CHARACTERS.length())));
        }
        return code.toString();
    }
    public String generateConfirmationToken() {
        log.info("Generating confirmation token uuid");
        return UUID.randomUUID().toString();
    }

    public void saveAuthCode(String emailOrPhone, String code) {
        log.info("Saving auth code for email or phone {}", emailOrPhone);
        cacheService.addAuthCode(emailOrPhone, code, codeExpirationSec);
    }

    public boolean validateAuthCode(String emailOrPhone, String code) {
        log.info("Валидация кода безопасности {}", code);
        String storedCode = cacheService.getAuthCode(emailOrPhone);
        return storedCode != null && storedCode.equals(code);
    }

    public void saveConfirmationToken(String token, String email) {
        cacheService.addConfirmationToken(token, email, linkExpirationSec); // 3 min expiration
        log.info("Токен подтверждения сохранен для: {}", email);
    }

    public void removeConfirmationToken(String token) {
        log.info("Удаление токена подтверждения для {}", getEmailByToken(token));
        cacheService.remove(CacheService.CONFIRMATION_PREFIX + token);
    }
    public String getEmailByToken(String token) {
        return cacheService.getEmailByConfirmationToken(token);
    }
}