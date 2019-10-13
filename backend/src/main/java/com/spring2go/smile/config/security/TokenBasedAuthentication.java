package com.spring2go.bookmarker.config.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

public class TokenBasedAuthentication extends AbstractAuthenticationToken {
    private final String token;
    private final UserDetails principal;

    public TokenBasedAuthentication(String token, UserDetails principal) {
        super(principal.getAuthorities());
        this.token = token;
        this.principal = principal;
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}
