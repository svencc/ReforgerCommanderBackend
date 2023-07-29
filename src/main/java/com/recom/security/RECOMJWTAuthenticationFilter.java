package com.recom.security;

import com.recom.security.jwt.JwtTokenAssertionService;
import com.recom.security.user.RECOMAuthentication;
import com.recom.security.user.RECOMUser;
import com.recom.security.user.RECOMUserAuthorities;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RECOMJWTAuthenticationFilter extends OncePerRequestFilter {


    @NonNull
    private final JwtDecoder jwtDecoder;
    @NonNull
    private final PublicEndpoints publicEndpoints;
    @NonNull
    private final JwtTokenAssertionService tokenAssertionService;
    @NonNull
    private final RECOMAuthenticationManager recomAuthenticationManager;


    @Override
    protected void doFilterInternal(
            @NonNull final HttpServletRequest request,
            @NonNull final HttpServletResponse response,
            @NonNull final FilterChain filterChain
    ) throws ServletException, IOException {
        if (publicEndpoints.publicEndpointsMatcher().matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        final Optional<String> authorizationHeaderOpt = Optional.ofNullable(request.getHeader("Authorization"));

        tokenAssertionService.assertAuthorizationHeaderIsPresent(authorizationHeaderOpt);
        tokenAssertionService.assertAuthorizationHeaderStartsWithBearer(authorizationHeaderOpt.get());

        Map<String, Object> headers;
        Map<String, Object> claims;
        try {
            final Jwt jwt = jwtDecoder.decode(extractToken(authorizationHeaderOpt.get()));
            headers = jwt.getHeaders();
            claims = jwt.getClaims();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        tokenAssertionService.assertTokenIsNotExpired((claims.get("exp")));
        tokenAssertionService.assertSubjectIsPresent(claims.get("sub"));
        final UUID user = tokenAssertionService.extractAndAssertSubjectIsUUID(claims.get("sub").toString());

        final String subject = claims.get("sub").toString();


        final RECOMUser recomUser = RECOMUser.builder()
                .userUuid(user)
                .accessKey("")
                .roles(Set.of(
                        RECOMUserAuthorities.AUTHORITY_TEST,
                        RECOMUserAuthorities.AUTHORITY_EVERYBODY
                ))
                .build();


        final RECOMAuthentication authentication = RECOMAuthentication.builder()
                .authorities(List.of(
                        new SimpleGrantedAuthority(RECOMUserAuthorities.AUTHORITY_TEST.name()),
                        new SimpleGrantedAuthority(RECOMUserAuthorities.AUTHORITY_EVERYBODY.name())
                ))
                .principal(recomUser)
//                .authenticated(true)
                .build();

        recomAuthenticationManager.authenticate(authentication);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    @NonNull
    private static String extractToken(@NonNull final String authorizationHeaderOpt) {
        return authorizationHeaderOpt.substring("Bearer".length()).trim();
    }


}
