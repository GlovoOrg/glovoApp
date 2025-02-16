package com.api.glovoCRM.Security.AuthenticationTokens;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class EmailAuthenticationToken extends AbstractAuthenticationToken {
    private final Object principal; // Почта
    private final Object credentials; // Пароль

    // Конструктор для НЕаутентифицированного токена (email + password)
    public EmailAuthenticationToken(Object principal, Object credentials) {
        super(null);
        this.principal = principal;
        this.credentials = credentials;
        setAuthenticated(false);
    }

    // Конструктор для аутентифицированного токена (с authorities)
    private EmailAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        super.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    // Статический метод для создания аутентифицированного токена
    public static EmailAuthenticationToken authenticated(
            Object principal,
            Object credentials,
            Collection<? extends GrantedAuthority> authorities
    ) {
        return new EmailAuthenticationToken(principal, credentials, authorities);
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) {
        if (isAuthenticated) {
            throw new IllegalArgumentException("Не используйте этот метод для установки аутентификации!");
        }
        super.setAuthenticated(false);
    }
}
