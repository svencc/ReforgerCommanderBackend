package com.recom.security;

import com.recom.entity.Account;
import com.recom.persistence.account.AccountPersistenceLayer;
import com.recom.security.jwt.JwtTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RECOMJWTAuthenticationFilter extends OncePerRequestFilter {

    @NonNull
    private final JwtDecoder jwtDecoder;
    @NonNull
    private final PublicEndpoints publicEndpoints;
    @NonNull
    private final JwtTokenService jwtTokenService;
    @NonNull
    private final AccountPersistenceLayer accountPersistenceLayer;
    @NonNull
    private final RECOMAuthenticationMapper authenticationMapper;


    @Override
    protected void doFilterInternal(
            @NonNull final HttpServletRequest request,
            @NonNull final HttpServletResponse response,
            @NonNull final FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            if (publicEndpoints.publicEndpointsMatcher().matches(request)) {
                filterChain.doFilter(request, response);
                return;
            }

            final Optional<String> authorizationHeaderOpt = Optional.ofNullable(request.getHeader("Authorization"));
            jwtTokenService.assertAuthorizationHeaderIsPresent(authorizationHeaderOpt);
            jwtTokenService.assertAuthorizationHeaderStartsWithBearer(authorizationHeaderOpt.get());

            final Jwt jwt = jwtDecoder.decode(jwtTokenService.extractToken(authorizationHeaderOpt.get()));

            jwtTokenService.assertTokenIsNotExpired((jwt.getClaims().get("exp")));
            jwtTokenService.assertClaimIsPresent(jwt.getClaims().get("sub"));

            final UUID subjectUUID = jwtTokenService.extractAndAssertSubjectIsUUID(jwt.getClaims().get("sub").toString());
            final Optional<Account> account = accountPersistenceLayer.findByUUID(subjectUUID);

            if (account.isPresent()) {
                SecurityContextHolder.getContext().setAuthentication(authenticationMapper.toAuthentication(account.get()));
                filterChain.doFilter(request, response);
            }
        } catch (Throwable e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

}
