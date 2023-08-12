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

            final String contentTypeHeader = jwtTokenService.assertIsPresent(Optional.ofNullable(request.getHeader("Content-Type")));

            Optional<String> authorizationOpt = Optional.empty();
            if (contentTypeHeader.equals("application/x-www-form-urlencoded")) {
                authorizationOpt = extractAuthorizationFromBody(request);
            } else if (contentTypeHeader.equals("application/json")) {
                authorizationOpt = Optional.ofNullable(request.getHeader("Authorization"));
            }

            final String authorization = jwtTokenService.assertIsPresent(authorizationOpt);
            jwtTokenService.assertAuthorizationStartsWithBearer(authorization);
            final Jwt jwt = jwtDecoder.decode(jwtTokenService.extractToken(authorization));

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

    @NonNull
    private Optional<String> extractAuthorizationFromBody(@NonNull final HttpServletRequest request) {
        final Optional<String> requestData = request.getParameterMap().keySet().stream().findFirst();

        if (requestData.isPresent()) {
            final Matcher matcher = pattern.matcher(requestData.get());
            if (matcher.find()) {
                return Optional.of(matcher.group(1));
            }
        }

        return Optional.empty();
    }

}
