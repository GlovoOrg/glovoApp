package com.api.glovoCRM.Security.jwt.Filter;

import com.api.glovoCRM.Exceptions.AuthExceptions.JwtExceptions.BlackListEx;
import com.api.glovoCRM.Models.UserModels.User;
import com.api.glovoCRM.Security.AuthenticationTokens.EmailAuthenticationToken;
import com.api.glovoCRM.Security.AuthenticationTokens.PhoneAuthenticationToken;
import com.api.glovoCRM.Security.AuthenticationTokens.PostAuthenticationTokenStuff;
import com.api.glovoCRM.Security.jwt.JwtCore;
import com.api.glovoCRM.Utils.Cache.BlackListService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtCore jwtCore;
    private final BlackListService blackListService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtCore jwtCore, BlackListService blackListService , UserDetailsService userDetailsService) {
        this.jwtCore = jwtCore;
        this.blackListService = blackListService;
        this.userDetailsService = userDetailsService;
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

                    if (SecurityContextHolder.getContext().getAuthentication() == null) {
                        AbstractAuthenticationToken authToken = createAuthToken((User) userDetails);
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
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
    private AbstractAuthenticationToken createAuthToken(User user) {
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            return EmailAuthenticationToken.postAuthenticated(
                    user.getEmail(),
                    null,
                    user.getAuthorities()
            );
        } else if (user.getPhoneNumber() != null && !user.getPhoneNumber().isEmpty()) {
            return PhoneAuthenticationToken.authenticated(
                    user.getPhoneNumber(),
                    null,
                    user.getAuthorities()
            );
        } else {
            return new PostAuthenticationTokenStuff(
                    user,
                    user.getAuthorities()
            );
        }
    }
}
