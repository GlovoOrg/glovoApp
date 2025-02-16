package com.api.glovoCRM.Security.jwt.Filter;

import com.api.glovoCRM.DAOs.UserDAOs.UserDAO;
import com.api.glovoCRM.Exceptions.AuthExceptions.JwtExceptions.BlackListEx;
import com.api.glovoCRM.Security.jwt.JwtCore;
import com.api.glovoCRM.Utils.Cache.BlackListService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtCore jwtCore;
    private final BlackListService blackListService;
    private final UserDetailsService userDetailsService;
    private final UserDAO userDAO;

    public JwtAuthenticationFilter(JwtCore jwtCore, BlackListService blackListService , UserDAO userDAO, UserDetailsService userDetailsService) {
        this.jwtCore = jwtCore;
        this.blackListService = blackListService;
        this.userDetailsService = userDetailsService;
        this.userDAO = userDAO;
    }


    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws IOException, ServletException {
        log.info("Обработка запроса в JwtAuthenticationFilter");
        String token = jwtCore.getTokenFromRequest(request);
        log.info("JWT-токен из запроса: {}", token);
        try {
            if (token != null) {
                blackListService.validateTokenNotBlacklisted(token);

                if (jwtCore.validateAccessToken(token)) {
                    String username = jwtCore.getSubjectFromAccessToken(token);
                    log.info("JWT-токен валиден для пользователя: {}", username);
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }catch (BlackListEx ex){
            log.error("Блэклистинг ошибка во время internal filter с токеном для:{}", jwtCore.getSubjectFromAccessToken(token));
            throw ex;
        }catch (Exception ex){
            log.error("Ошибка аутентификации: {}", ex.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Authentication error");
        }
        filterChain.doFilter(request, response);
    }
}
