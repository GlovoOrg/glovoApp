package com.api.glovoCRM.Security.AuthenticationTokens;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class PhoneAuthenticationToken extends AbstractAuthenticationToken {
    private final Object principal; // Номер телефона
    private final Object credentials; // password

    // Конструктор для НЕаутентифицированного токена
    public PhoneAuthenticationToken(Object principal, Object credentials) {
        super(null);
        this.principal = principal;
        this.credentials = credentials;
        setAuthenticated(false);
    }

    // Конструктор для аутентифицированного токена
    public PhoneAuthenticationToken(Object principal, Object credentials,
                                    Collection<? extends GrantedAuthority> authorities) {
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

    public static PhoneAuthenticationToken authenticated(
            Object principal,
            Object credentials,
            Collection<? extends GrantedAuthority> authorities
    ) {
        if (principal == null) {
            throw new IllegalArgumentException("Principal не могут быть null");
        }
        return new PhoneAuthenticationToken(principal, credentials, authorities);
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) {
        if (isAuthenticated) {
            throw new IllegalArgumentException("Не используйте этот метод для установки аутентификации!");
        }
        super.setAuthenticated(false);
    }
}
