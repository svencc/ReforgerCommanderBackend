package com.recom.security;

import com.recom.entity.Account;
import com.recom.security.account.RECOMAuthentication;
import com.recom.security.account.RECOMUser;
import com.recom.security.account.RECOMUserAuthorities;
import lombok.NonNull;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class RECOMAuthenticationMapper {

    @NonNull
    public RECOMAuthentication toAuthentication(@NonNull final Account user) {
        final RECOMUser recomUser = RECOMUser.builder()
                .userUuid(user.getAccountUuid())
                .accessKey(user.getAccessKey())
                .roles(Set.of(
                        RECOMUserAuthorities.AUTHORITY_TEST,
                        RECOMUserAuthorities.AUTHORITY_EVERYBODY
                ))
                .build();

        final RECOMAuthentication authentication = RECOMAuthentication.builder()
                .authorities(recomUser.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.name()))
                        .toList()
                )
                .principal(recomUser)
                .authenticated(true)
                .build();

        return authentication;
    }

}
