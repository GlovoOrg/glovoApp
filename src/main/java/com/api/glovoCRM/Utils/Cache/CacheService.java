package com.api.glovoCRM.Utils.Cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class CacheService {
    public static final String BLACKLIST_PREFIX = "blacklist:";
    public static final String CONFIRMATION_PREFIX = "confirmation:";
    public static final String AUTH_CODE_PREFIX = "auth_code:";

    private final RedisTemplate<String, Object> redisTemplate;

    public void addBlacklistedToken(String token, long expiresIn) {
        if(expiresIn <= 0) {
            log.warn("Попытка добавить токен с истекшим сроком: {}", maskToken(token));
            return;
        }
        String key = BLACKLIST_PREFIX + token;
        redisTemplate.opsForValue().set(key, true, expiresIn, TimeUnit.SECONDS);
        log.info("Токен добавлен в черный список: {}", maskToken(token));
    }

    public boolean isTokenBlacklisted(String token) {
        String key = BLACKLIST_PREFIX + token;
        Boolean exists = redisTemplate.hasKey(key);
        log.info("Проверка токена в черном списке: {} -> {}", maskToken(token), exists);
        return Boolean.TRUE.equals(exists);
    }

    public void addConfirmationToken(String token, String email, long expiresIn) {
        String key = CONFIRMATION_PREFIX + token;
        redisTemplate.opsForValue().set(key, email, expiresIn, TimeUnit.SECONDS);
        log.info("Токен подтверждения сохранен для email: {}", maskEmail(email));
    }

    public String getEmailByConfirmationToken(String token) {
        String key = CONFIRMATION_PREFIX + token;
        String email = (String) redisTemplate.opsForValue().get(key);
        log.info("Получен email по токену подтверждения: {}", maskEmail(email));
        return email;
    }

    public void addAuthCode(String key, String code, long expiresIn) {
        String cacheKey = AUTH_CODE_PREFIX + key;
        redisTemplate.opsForValue().set(cacheKey, code, expiresIn, TimeUnit.SECONDS);
        log.info("Код аутентификации сохранен для: {}", key);;
    }

    public String getAuthCode(String key) {
        String cacheKey = AUTH_CODE_PREFIX + key;
        String code = (String) redisTemplate.opsForValue().get(cacheKey);
        log.info("Запрос кода аутентификации для: {}", key);
        return code;
    }

    public void remove(String key) {
        redisTemplate.delete(key);
        log.info("Данные удалены из кэша по ключу: {}", key);
    }

    public String maskToken(String token) {
        if(token == null || token.length() < 8) return "****";
        return token.substring(0,4) + "****" + token.substring(token.length()-4);
    }

    public String maskEmail(String email) {
        if(email == null || !email.contains("@")) return "****";
        String[] parts = email.split("@");
        if(parts[0].length() > 3) {
            return parts[0].substring(0,3) + "***@" + parts[1];
        }
        return "***@" + parts[1];
    }
}
