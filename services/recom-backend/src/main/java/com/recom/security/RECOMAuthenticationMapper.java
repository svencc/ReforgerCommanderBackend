package com.recom.security;

import com.recom.entity.Account;
import com.recom.security.account.RECOMAccount;
import com.recom.security.account.RECOMAuthentication;
import com.recom.security.account.RECOMAuthorities;
import lombok.NonNull;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class RECOMAuthenticationMapper {

    @NonNull
    public RECOMAuthentication toAuthentication(@NonNull final Account user) {
        final RECOMAccount recomAccount = RECOMAccount.builder()
                .accountUuid(user.getAccountUuid())
                .accessKey(user.getAccessKey())
                .roles(Set.of(
                        RECOMAuthorities.EVERYBODY
                ))
                .build();

        final RECOMAuthentication authentication = RECOMAuthentication.builder()
                .authorities(recomAccount.getRoles().stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList()
                )
                .principal(recomAccount)
                .authenticated(true)
                .build();

        return authentication;
    }

}
