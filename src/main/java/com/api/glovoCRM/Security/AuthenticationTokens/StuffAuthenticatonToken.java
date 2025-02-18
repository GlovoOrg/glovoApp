//package com.api.glovoCRM.Security.AuthenticationTokens;
//
//import org.springframework.security.authentication.AbstractAuthenticationToken;
//import org.springframework.security.core.GrantedAuthority;
//
//import java.util.Collection;
//
//public class StuffAuthenticatonToken  extends AbstractAuthenticationToken {
//    private final Object credentials;
//    private final Object principal;
//
//    @Override
//    public Object getCredentials() {
//        return credentials;
//    }
//
//    @Override
//    public Object getPrincipal() {
//        return principal;
//    }
//    public StuffAuthenticatonToken(Object credentials, Object principal) {
//        super(null);
//        this.principal = principal;
//        this.credentials = credentials;
//        setAuthenticated(false);
//    }
//    public StuffAuthenticatonToken(Object credentials, Object principal, Collection<? extends GrantedAuthority> authorities) {
//        super(authorities);
//        this.principal = principal;
//        this.credentials = credentials;
//        super.setAuthenticated(true);
//    }
//    public static StuffAuthenticatonToken authenticated(
//            Object credentials, Object principal, Collection<? extends GrantedAuthority> authorities) {
//        return new StuffAuthenticatonToken(credentials, principal, authorities);
//    }
//    @Override
//    public void setAuthenticated(boolean isAuthenticated) {
//        if (isAuthenticated) {
//            throw new IllegalArgumentException("Не используйте этот метод для установки аутентификации!");
//        }
//        super.setAuthenticated(false);
//    }
//}
