package com.recom.security;

import com.recom.entity.Account;
import com.recom.persistence.account.AccountPersistenceLayer;
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
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

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
    private final AccountPersistenceLayer accountPersistenceLayer;


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

            tokenAssertionService.assertAuthorizationHeaderIsPresent(authorizationHeaderOpt);
            tokenAssertionService.assertAuthorizationHeaderStartsWithBearer(authorizationHeaderOpt.get());

            final Jwt jwt = jwtDecoder.decode(extractToken(authorizationHeaderOpt.get()));
            final Map<String, Object> headers = jwt.getHeaders();
            final Map<String, Object> claims = jwt.getClaims();

            tokenAssertionService.assertTokenIsNotExpired((claims.get("exp")));
            tokenAssertionService.assertSubjectIsPresent(claims.get("sub"));
            final UUID subjectUUID = tokenAssertionService.extractAndAssertSubjectIsUUID(claims.get("sub").toString());

            final Optional<Account> user = accountPersistenceLayer.findByUUID(subjectUUID);

            if (user.isPresent()) {
                final RECOMUser recomUser = RECOMUser.builder()
                        .userUuid(user.get().getAccountUuid())
                        .accessKey(user.get().getAccessKey())
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
                SecurityContextHolder.getContext().setAuthentication(authentication);
                filterChain.doFilter(request, response);
            }
        } catch (Throwable e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    @NonNull
    private static String extractToken(@NonNull final String authorizationHeaderOpt) {
        return authorizationHeaderOpt.substring("Bearer".length()).trim();
    }


}
