package com.api.glovoCRM.Services.AuthServices;

import com.api.glovoCRM.DAOs.RefreshTokenDAO;
import com.api.glovoCRM.DAOs.UserDAOs.UserDAO;
import com.api.glovoCRM.Models.UserModels.RefreshToken;
import com.api.glovoCRM.Models.UserModels.User;
import com.api.glovoCRM.Security.jwt.JwtCore;
import com.api.glovoCRM.Utils.Cache.BlackListService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;

@Service
public class TokenService {

    private final JwtCore jwtCore;
    private final RefreshTokenDAO refreshTokenDAO;
    private final UserDAO userDAO;
    private final BlackListService blackListService;

    @Autowired
    public TokenService(JwtCore jwtCore, RefreshTokenDAO refreshTokenDAO, UserDAO userDAO, BlackListService blackListService) {
        this.jwtCore = jwtCore;
        this.refreshTokenDAO = refreshTokenDAO;
        this.userDAO = userDAO;
        this.blackListService = blackListService;
    }
    @Transactional
    public Map<String, String> generateTokens(User user) {
        String accessToken = jwtCore.generateAccessToken(user);
        String refreshToken = jwtCore.generateRefreshToken(user);

        RefreshToken existingToken = refreshTokenDAO.findByUserId(user.getId()).orElse(null);

        if (existingToken != null) {
            existingToken.setToken(refreshToken);
            existingToken.setExpiryDate(Instant.now().plusSeconds(jwtCore.getRefreshExpiration()));
            refreshTokenDAO.save(existingToken);
        } else {
            RefreshToken refreshTokenEntity = new RefreshToken();
            refreshTokenEntity.setUser(user);
            refreshTokenEntity.setToken(refreshToken);
            refreshTokenEntity.setExpiryDate(Instant.now().plusSeconds(jwtCore.getRefreshExpiration()));
            refreshTokenDAO.save(refreshTokenEntity);
        }

        return Map.of(
                "access_token", accessToken,
                "refresh_token", refreshToken
        );
    }
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public Map<String, String> refreshAccessToken(String refreshToken) {
        try {
            blackListService.validateTokenNotBlacklisted(refreshToken);
            if (!jwtCore.validateRefreshToken(refreshToken)) {
                throw new RuntimeException("Недействительный refresh token");
            }
            String name = jwtCore.getSubjectFromRefreshToken(refreshToken);

            User user = userDAO.findByName(name)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
            String newAccessToken = jwtCore.generateAccessToken(user);

            return Map.of("access_token", newAccessToken);

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}