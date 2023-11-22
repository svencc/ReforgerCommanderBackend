package com.recom.security.account;

import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import javax.security.auth.Subject;
import java.util.Collection;

@Data
@Builder
public class RECOMAuthentication implements Authentication {

    private Collection<? extends GrantedAuthority> authorities;
    private Object credentials;
    private Object details;
    private RECOMAccount principal;
    private boolean authenticated;
    private String name;

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }

    // @TODO generate user on the fly?
//    @Override
//    public RECOMUser getPrincipal() {
//        return this;
//    }

    @Override
    public boolean implies(final Subject subject) {
        return Authentication.super.implies(subject);
    }

}
