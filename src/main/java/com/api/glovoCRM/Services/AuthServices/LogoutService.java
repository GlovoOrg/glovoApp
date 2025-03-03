package com.api.glovoCRM.Services.AuthServices;

import com.api.glovoCRM.Repositories.RefreshTokenRepository;
import com.api.glovoCRM.Repositories.UserDAOs.UserRepository;
import com.api.glovoCRM.Exceptions.BaseExceptions.SuchResourceNotFoundEx;
import com.api.glovoCRM.Models.UserModels.RefreshToken;
import com.api.glovoCRM.Security.jwt.JwtCore;
import com.api.glovoCRM.Utils.Cache.BlackListService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LogoutService {
    private final BlackListService blackListService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtCore jwtCore;


    @Autowired
    public LogoutService(BlackListService blackListService, RefreshTokenRepository refreshTokenRepository, UserRepository userRepository, JwtCore jwtCore) {
        this.blackListService = blackListService;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.jwtCore = jwtCore;
    }
    public void logout(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Невалидный заголовок Authorization");
        }

        String accessToken= authorizationHeader.substring(7);
        try {
            String name = jwtCore.getSubjectFromAccessToken(accessToken);
            Long userId = userRepository.findByName(name)
                    .orElseThrow(() -> new SuchResourceNotFoundEx("Пользователь не найден: " + name))
                    .getId();
            RefreshToken refreshTokenEntity = refreshTokenRepository.findByUserId(userId)
                    .orElseThrow(() -> new SuchResourceNotFoundEx("Рефреш Токен не найден  для пользователя" + userId));

            blackListService.addToBlacklist(refreshTokenEntity.getToken());
            refreshTokenRepository.delete(refreshTokenEntity);
            SecurityContextHolder.clearContext();
            log.info("Пользователь {} успешно вышел", name);
        } catch (Exception e) {
            log.error("Ошибка при выходе пользователя: {}", e.getMessage());
            throw e;
        }
    }
}
