package com.api.glovoCRM.Security.AuthenticationTokens;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;

public class PostAuthenticationTokenStuff extends AbstractAuthenticationToken {
    private final Object principal;

    public PostAuthenticationTokenStuff(Object principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities != null ? authorities : Collections.emptyList());
        if (principal == null) {
            throw new IllegalArgumentException("Principal  cannot be null");
        }
        this.principal = principal;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}
