package com.api.glovoCRM.Security.AuthenticationTokens;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.Collections;

public class PreAuthenticationTokenStuff extends AbstractAuthenticationToken {
    private final Object principal;
    private final Object credentials;

    public PreAuthenticationTokenStuff(Object principal, Object credentials) {
        super(Collections.emptyList());
        if (principal == null) {
            throw new IllegalArgumentException("Principal cannot be null");
        }
        this.principal = principal;
        this.credentials = credentials;
        setAuthenticated(false);
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}
