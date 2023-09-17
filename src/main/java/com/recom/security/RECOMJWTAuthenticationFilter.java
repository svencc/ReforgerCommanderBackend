package com.recom.security;

import com.recom.entity.Account;
import com.recom.persistence.account.AccountPersistenceLayer;
import com.recom.security.jwt.JwtTokenService;
import jakarta.annotation.PostConstruct;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class RECOMJWTAuthenticationFilter extends OncePerRequestFilter {

    @NonNull
    private final JwtDecoder jwtDecoder;
    @NonNull
    private final PublicEndpointsProvider publicEndpointsProvider;
    @NonNull
    private final JwtTokenService jwtTokenService;
    @NonNull
    private final AccountPersistenceLayer accountPersistenceLayer;
    @NonNull
    private final RECOMAuthenticationMapper authenticationMapper;

    private Pattern pattern;

    @PostConstruct
    public void postConstruct() {
        pattern = Pattern.compile("\"Authorization\":\"(.*?)\"");
    }

    @Override
    protected void doFilterInternal(
            @NonNull final HttpServletRequest request,
            @NonNull final HttpServletResponse response,
            @NonNull final FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            if (publicEndpointsProvider.publicEndpointsMatcher().matches(request)) {
                filterChain.doFilter(request, response);
                return;
            }

            final Optional<String> maybeContentType = Optional.ofNullable(request.getHeader("Content-Type"));

            Optional<String> maybeAuthorization = Optional.empty();
            if (maybeContentType.isPresent() && maybeContentType.get().equals("application/x-www-form-urlencoded")) {
                maybeAuthorization = extractAuthorizationFromBody(request);
            } else {
                maybeAuthorization = Optional.ofNullable(request.getHeader("Authorization"));
            }

            // @TODO check if token is expired? necessary here?
            // jwtTokenService.assertTokenIsNotExpired(jwt.getJWTClaimsSet().getClaims().get("exp"));

            final String authorization = jwtTokenService.passThroughIfPresent(maybeAuthorization);
            jwtTokenService.assertAuthorizationStartsWithBearer(authorization);
            final Jwt jwt = jwtDecoder.decode(jwtTokenService.extractToken(authorization));

            final String claim = jwtTokenService.passThroughIfClaimIsPresent(jwt.getClaims().get("sub"));
            final UUID subjectUUID = jwtTokenService.extractAndAssertSubjectIsUUID(claim);
            final Optional<Account> maybeAccount = accountPersistenceLayer.findByUUID(subjectUUID);

            if (maybeAccount.isPresent()) {
                SecurityContextHolder.getContext().setAuthentication(authenticationMapper.toAuthentication(maybeAccount.get()));
                filterChain.doFilter(request, response);
            }
        } catch (Throwable e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    @NonNull
    private Optional<String> extractAuthorizationFromBody(@NonNull final HttpServletRequest request) {
        final Optional<String> maybeRequestData = request.getParameterMap().keySet().stream().findFirst();

        if (maybeRequestData.isPresent()) {
            final Matcher matcher = pattern.matcher(maybeRequestData.get());
            if (matcher.find()) {
                return Optional.of(matcher.group(1));
            }
        }

        return Optional.empty();
    }

}
