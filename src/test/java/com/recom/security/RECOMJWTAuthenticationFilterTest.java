package com.recom.security;

import com.recom.entity.Account;
import com.recom.persistence.account.AccountPersistenceLayer;
import com.recom.security.account.RECOMAuthentication;
import com.recom.security.jwt.JwtTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RECOMJWTAuthenticationFilterTest {

    @Test
    public void testDoFilterInternal_WithPublicEndpoint() throws IOException, ServletException {
        // Arrange
        final JwtDecoder jwtDecoder = mock(JwtDecoder.class);
        final JwtTokenService jwtTokenService = mock(JwtTokenService.class);
        final AccountPersistenceLayer accountPersistenceLayer = mock(AccountPersistenceLayer.class);
        final RECOMAuthenticationMapper authenticationMapper = mock(RECOMAuthenticationMapper.class);

        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final FilterChain filterChain = mock(FilterChain.class);
        final PublicEndpointsProvider publicEndpointsProvider = mock(PublicEndpointsProvider.class);
        when(publicEndpointsProvider.publicEndpointsMatcher()).thenReturn(AnyRequestMatcher.INSTANCE);

        final RECOMJWTAuthenticationFilter filter = new TestRECOMJWTAuthenticationFilter(
                jwtDecoder, publicEndpointsProvider, jwtTokenService, accountPersistenceLayer, authenticationMapper
        );

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void testDoFilterInternal_withJSONAuthentication_success() throws IOException, ServletException {
        // Arrange
        final UUID accountUuid = UUID.randomUUID();
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final FilterChain filterChain = mock(FilterChain.class);
        final PublicEndpointsProvider publicEndpointsProvider = mock(PublicEndpointsProvider.class);
        final AccountPersistenceLayer accountPersistenceLayer = mock(AccountPersistenceLayer.class);
        final RECOMAuthenticationMapper authenticationMapper = mock(RECOMAuthenticationMapper.class);
        final JwtDecoder jwtDecoder = mock(JwtDecoder.class);
        final JwtTokenService jwtTokenService = mock(JwtTokenService.class);
        final Jwt jwt = mock(Jwt.class);
        final Account account = new Account();

        when(publicEndpointsProvider.publicEndpointsMatcher()).thenReturn(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/swagger-ui.html"));

        when(request.getHeader(eq("Content-Type"))).thenReturn("application/json");

        final List<String> returnValues = new ArrayList<>();
        returnValues.add("application/json");
        returnValues.add("Bearer TOKEN");
        when(jwtTokenService.assertIsPresent(any())).thenAnswer(invocation -> returnValues.remove(0));

        when(jwtTokenService.extractToken(any())).thenReturn("TOKEN");
        when(jwtDecoder.decode(eq("TOKEN"))).thenReturn(jwt);
        when(jwt.getClaims()).thenReturn(Collections.singletonMap("sub", accountUuid));
        jwtTokenService.assertClaimIsPresent(eq(accountUuid));
        when(jwtTokenService.extractAndAssertSubjectIsUUID(any())).thenReturn(accountUuid);
        when(accountPersistenceLayer.findByUUID(eq(accountUuid))).thenReturn(Optional.of(account));
        when(authenticationMapper.toAuthentication(any(Account.class))).thenReturn(RECOMAuthentication.builder().authenticated(true).build());

        final RECOMJWTAuthenticationFilter filter = new TestRECOMJWTAuthenticationFilter(
                jwtDecoder, publicEndpointsProvider, jwtTokenService, accountPersistenceLayer, authenticationMapper
        );

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        assertTrue(SecurityContextHolder.getContext().getAuthentication().isAuthenticated());
    }

    @Test
    public void testDoFilterInternal_withFORMAuthentication_success() throws IOException, ServletException {
        // Arrange
        final UUID accountUuid = UUID.randomUUID();
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final FilterChain filterChain = mock(FilterChain.class);
        final PublicEndpointsProvider publicEndpointsProvider = mock(PublicEndpointsProvider.class);
        final AccountPersistenceLayer accountPersistenceLayer = mock(AccountPersistenceLayer.class);
        final RECOMAuthenticationMapper authenticationMapper = mock(RECOMAuthenticationMapper.class);
        final JwtDecoder jwtDecoder = mock(JwtDecoder.class);
        final JwtTokenService jwtTokenService = mock(JwtTokenService.class);
        final Jwt jwt = mock(Jwt.class);
        final Account account = new Account();

        when(publicEndpointsProvider.publicEndpointsMatcher()).thenReturn(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/swagger-ui.html"));

        when(request.getHeader(eq("Content-Type"))).thenReturn("application/x-www-form-urlencoded");

        Map<String, String[]> parameterMap = new HashMap<>();
        parameterMap.put("{\"Authorization\":\"Bearer TOKEN\"...}", new String[]{""});
        when(request.getParameterMap()).thenReturn(parameterMap);

        final List<String> returnValues = new ArrayList<>();
        returnValues.add("application/x-www-form-urlencoded");
        returnValues.add("Bearer TOKEN");
        when(jwtTokenService.assertIsPresent(any())).thenAnswer(invocation -> returnValues.remove(0));

        when(jwtTokenService.extractToken(any())).thenReturn("TOKEN");
        when(jwtDecoder.decode(eq("TOKEN"))).thenReturn(jwt);
        when(jwt.getClaims()).thenReturn(Collections.singletonMap("sub", accountUuid));
        jwtTokenService.assertClaimIsPresent(eq(accountUuid));
        when(jwtTokenService.extractAndAssertSubjectIsUUID(any())).thenReturn(accountUuid);
        when(accountPersistenceLayer.findByUUID(eq(accountUuid))).thenReturn(Optional.of(account));
        when(authenticationMapper.toAuthentication(any(Account.class))).thenReturn(RECOMAuthentication.builder().authenticated(true).build());

        final RECOMJWTAuthenticationFilter filter = new TestRECOMJWTAuthenticationFilter(
                jwtDecoder, publicEndpointsProvider, jwtTokenService, accountPersistenceLayer, authenticationMapper
        );
        filter.postConstruct();

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        assertTrue(SecurityContextHolder.getContext().getAuthentication().isAuthenticated());
    }

    @Test
    public void testDoFilterInternal_Unauthenticated() throws IOException, ServletException {
        // Arrange
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final FilterChain filterChain = mock(FilterChain.class);
        final PublicEndpointsProvider publicEndpointsProvider = mock(PublicEndpointsProvider.class);
        when(request.getMethod()).thenReturn(HttpMethod.GET.name());
        when(publicEndpointsProvider.publicEndpointsMatcher()).thenReturn(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/swagger-ui.html"));

        final JwtDecoder jwtDecoder = mock(JwtDecoder.class);
        final JwtTokenService jwtTokenService = mock(JwtTokenService.class);
        final AccountPersistenceLayer accountPersistenceLayer = mock(AccountPersistenceLayer.class);
        final RECOMAuthenticationMapper authenticationMapper = mock(RECOMAuthenticationMapper.class);

        final RECOMJWTAuthenticationFilter filterToTest = new TestRECOMJWTAuthenticationFilter(
                jwtDecoder, publicEndpointsProvider, jwtTokenService, accountPersistenceLayer, authenticationMapper
        );

        // Act
        filterToTest.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, never()).doFilter(request, response);
        verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }


    // implement RECOMJWTAuthenticationFilter to test protected method doFilterInternal
    private static class TestRECOMJWTAuthenticationFilter extends RECOMJWTAuthenticationFilter {

        public TestRECOMJWTAuthenticationFilter(
                final JwtDecoder jwtDecoder,
                final PublicEndpointsProvider publicEndpointsProvider,
                final JwtTokenService jwtTokenService,
                final AccountPersistenceLayer accountPersistenceLayer,
                final RECOMAuthenticationMapper authenticationMapper
        ) {
            super(jwtDecoder, publicEndpointsProvider, jwtTokenService, accountPersistenceLayer, authenticationMapper);
        }

        @Override
        protected void doFilterInternal(
                final HttpServletRequest request,
                final HttpServletResponse response,
                final FilterChain filterChain
        ) throws ServletException, IOException {
            super.doFilterInternal(request, response, filterChain);
        }

    }

}
