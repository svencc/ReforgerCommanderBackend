package com.recom.security;

import com.recom.security.user.RECOMUserDetailsService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class RECOMAuthenticationManager implements AuthenticationManager {

    @NonNull
    private final RECOMUserDetailsService recomUserDetailsService;


    @Override
    public Authentication authenticate(@NonNull final Authentication authentication) throws AuthenticationException {
        final UserDetails userDetails = recomUserDetailsService.loadUserByUsername(authentication.getName());
        authentication.setAuthenticated(true);

        return authentication;
    }

}
