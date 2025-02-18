package com.api.glovoCRM.Services.AuthServices;

import com.api.glovoCRM.DAOs.RefreshTokenDAO;
import com.api.glovoCRM.DAOs.UserDAOs.UserDAO;
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
    private final RefreshTokenDAO refreshTokenDAO;
    private final UserDAO userDAO;
    private final JwtCore jwtCore;


    @Autowired
    public LogoutService(BlackListService blackListService, RefreshTokenDAO refreshTokenDAO, UserDAO userDAO, JwtCore jwtCore) {
        this.blackListService = blackListService;
        this.refreshTokenDAO = refreshTokenDAO;
        this.userDAO = userDAO;
        this.jwtCore = jwtCore;
    }
    public void logout(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Невалидный заголовок Authorization");
        }

        String accessToken= authorizationHeader.substring(7);
        try {
            // Проверяем, существует ли токен в базе данных
            String name = jwtCore.getSubjectFromAccessToken(accessToken);
            Long userId = userDAO.findByName(name)
                    .orElseThrow(() -> new SuchResourceNotFoundEx("Пользователь не найден: " + name))
                    .getId();
            RefreshToken refreshTokenEntity = refreshTokenDAO.findByUserId(userId)
                    .orElseThrow(() -> new SuchResourceNotFoundEx("Рефреш Токен не найден  для пользователя" + userId));

            // Добавляем токен в черный список
            blackListService.addToBlacklist(refreshTokenEntity.getToken());
            refreshTokenDAO.delete(refreshTokenEntity);
            //Контекстик чистим
            SecurityContextHolder.clearContext();
            log.info("Пользователь {} успешно вышел", name);
        } catch (Exception e) {
            log.error("Ошибка при выходе пользователя: {}", e.getMessage());
            throw e;
        }
    }
}
