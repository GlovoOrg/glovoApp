package com.api.glovoCRM.Utils.Cache;

import com.api.glovoCRM.Exceptions.AuthExceptions.JwtExceptions.BlackListEx;
import com.api.glovoCRM.Security.jwt.JwtCore;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
@RequiredArgsConstructor
public class BlackListService {
    private final CacheService cacheService;
    private final JwtCore jwtCore;

    public void addToBlacklist(String token) {
        try {
            Claims claims = jwtCore.getClaimsFromRefreshToken(token);
            Date expiration = claims.getExpiration();
            long expiresIn = (expiration.getTime() - System.currentTimeMillis()) / 1000;

            if (expiresIn > 0) {
                cacheService.addBlacklistedToken(token, expiresIn);
                log.info("Токен заблокирован: {}", cacheService.maskToken(token));
            }
        } catch (Exception e) {
            log.error("Ошибка блокировки токена: {}", e.getMessage());
            throw new BlackListEx("Ошибка блокировки токена");
        }
    }

    public void validateTokenNotBlacklisted(String token) {
        if (cacheService.isTokenBlacklisted(token)) {
            log.warn("Обнаружен заблокированный токен: {}", cacheService.maskToken(token));
            throw new BlackListEx("Токен в черном списке");
        }
    }
}
