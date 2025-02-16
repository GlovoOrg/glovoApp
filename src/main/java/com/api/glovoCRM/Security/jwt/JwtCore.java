package com.api.glovoCRM.Security.jwt;

import com.api.glovoCRM.Exceptions.AuthExceptions.JwtExceptions.InvalidTokenEx;
import com.api.glovoCRM.Exceptions.AuthExceptions.JwtExceptions.TokenExpiredEx;
import com.api.glovoCRM.Exceptions.AuthExceptions.JwtExceptions.TokenParsingEx;
import com.api.glovoCRM.Models.UserModels.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.io.Decoders;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@Slf4j
public class JwtCore {

    private final SecretKey accessSecretKey;
    private final SecretKey refreshSecretKey;

    private final int accessExpiration;
    @Getter
    private final int refreshExpiration;

    @Autowired
    public JwtCore(
            @Value("${jwt.access.secret}") String jwtAccess,
            @Value("${jwt.refresh.secret}") String jwtRefresh,
            @Value("${jwt.access.expiration}") int accessExpiration,
            @Value("${jwt.refresh.expiration}") int refreshExpiration) {
        this.accessSecretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtAccess));
        this.refreshSecretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtRefresh));
        this.accessExpiration = accessExpiration;
        this.refreshExpiration = refreshExpiration;
    }
    public String getTokenFromRequest(HttpServletRequest request) {
        final String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            log.info("Токен успешно извлечен из заголовка запроса.");
            return header.substring(7);
        }
        log.warn("Заголовок 'Authorization' отсутствует или не содержит Bearer токен.");
        return null;
    }

    public String generateAccessToken(User userDetail) {
        Date now = new Date(System.currentTimeMillis());
        Date expirationAccessInstant = new Date(now.getTime() + accessExpiration);
        log.info("Генерация нового Access Token для пользователя: {}", userDetail.getUsername());
        return Jwts.builder()
                .subject(userDetail.getUsername())
                .issuedAt(now)
                .expiration(expirationAccessInstant)
                .signWith(accessSecretKey)
                .claim("roles", userDetail.getAuthorities())
                .compact();
    }

    public String generateRefreshToken(User userDetail) {
        Date now = new Date(System.currentTimeMillis());
        Date expirationRefreshInstant = new Date(now.getTime() + refreshExpiration);
        log.info("Генерация нового Refresh Token для пользователя: {}", userDetail.getUsername());
        return Jwts.builder()
                .issuedAt(now)
                .subject(userDetail.getUsername())
                .expiration(expirationRefreshInstant)
                .signWith(refreshSecretKey)
                .claim("roles", userDetail.getAuthorities())
                .compact();
    }

    public boolean validateAccessToken(String accessToken) {
        return validateToken(accessToken, accessSecretKey);
    }

    public boolean validateRefreshToken(String refreshToken) {
        return validateToken(refreshToken, refreshSecretKey);
    }

    private boolean validateToken(String token, SecretKey key) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            log.info("Токен успешно провалидирован");
            return true;
        } catch (ExpiredJwtException e) {
            log.error("Ошибка валидации токена: срок действия истек.");
            throw new TokenExpiredEx("Срок действия токена истек.");
        } catch (UnsupportedJwtException e) {
            log.error("Ошибка валидации токена: неподдерживаемый формат.");
            throw new InvalidTokenEx("Неподдерживаемый формат токена.");
        } catch (MalformedJwtException e) {
            log.error("Ошибка валидации токена: неверный формат.");
            throw new InvalidTokenEx("Неверный формат токена.");
        } catch (SignatureException e) {
            log.error("Ошибка валидации токена: неверная подпись.");
            throw new InvalidTokenEx("Неверная подпись токена.");
        } catch (IllegalArgumentException e) {
            log.error("Ошибка валидации токена: некорректные данные.");
            throw new TokenParsingEx("Некорректные данные в токене.");
        }
    }

    public Claims getClaimsFromAccessToken(String accessToken) {
        Claims claims = getClaimsFromToken(accessToken, accessSecretKey);
        return claims;
    }

    public Claims getClaimsFromRefreshToken(String refreshToken) {
        Claims claims = getClaimsFromToken(refreshToken, refreshSecretKey);
        return claims;
    }

    private Claims getClaimsFromToken(String token, SecretKey key) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException e) {
            log.error("Ошибка пока экстрактили клэймы из аксеса: {}", e.getMessage());
        }
        return null;
    }

    public boolean isRefreshTokenExpired(String refreshToken) {
        log.info("Проверка срока действия Refresh Token.");
        return isExpired(refreshToken, refreshSecretKey);
    }

    public boolean isAccessTokenExpired(String accessToken) {
        log.info("Проверка срока действия Access Token.");
        return isExpired(accessToken, accessSecretKey);
    }

    public boolean isExpired(String token, SecretKey secretKey) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getExpiration().before(new Date());

        } catch (ExpiredJwtException e) {
            log.error("Токен истек: {}", e.getMessage());
            throw new TokenExpiredEx("Token истек");
        }catch (JwtException e) {
            log.error("Ошибка при проверке срока действия токена: {}", e.getMessage());
            throw new TokenParsingEx("Ошибка при проверке срока действия токена.");
        }
    }
    public String getSubjectFromToken(String token, SecretKey secretKey){
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
    public String getSubjectFromRefreshToken(String refreshToken){
        log.info("Получение subject из Refresh Token.");
        return getSubjectFromToken(refreshToken, refreshSecretKey);
    }
    public String getSubjectFromAccessToken(String accessToken){
        log.info("Получение subject из Access Token.");
        return getSubjectFromToken(accessToken, accessSecretKey);
    }
}
