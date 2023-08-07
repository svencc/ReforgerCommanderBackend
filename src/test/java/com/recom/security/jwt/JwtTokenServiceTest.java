package com.recom.security.jwt;

import com.recom.exception.HttpUnauthorizedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.ConversionException;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.ConversionService;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtTokenServiceTest {

    @Mock
    private ConversionService conversionService;
    @InjectMocks
    private JwtTokenService jwtTokenService;


    @Test
    public void testAssertAuthorizationHeaderIsPresent_ValidHeader() {
        // Arrange
        Optional<String> authorizationHeader = Optional.of("Bearer token");

        // Act & Assert
        assertDoesNotThrow(() -> jwtTokenService.assertAuthorizationHeaderIsPresent(authorizationHeader));
    }

    @Test
    public void testAssertAuthorizationHeaderIsPresent_InvalidHeader() {
        // Arrange
        Optional<String> authorizationHeader = Optional.empty();

        // Act & Assert
        HttpUnauthorizedException exception = assertThrows(HttpUnauthorizedException.class,
                () -> jwtTokenService.assertAuthorizationHeaderIsPresent(authorizationHeader));
        assertEquals("Authorization header is not present", exception.getMessage());
    }

    @Test
    public void testAssertAuthorizationHeaderStartsWithBearer_ValidHeader() {
        // Arrange
        String authorizationHeader = "Bearer token";

        // Act & Assert
        assertDoesNotThrow(() -> jwtTokenService.assertAuthorizationHeaderStartsWithBearer(authorizationHeader));
    }

    @Test
    public void testAssertAuthorizationHeaderStartsWithBearer_InvalidHeader() {
        // Arrange
        String authorizationHeader = "Token token";

        // Act & Assert
        HttpUnauthorizedException exception = assertThrows(HttpUnauthorizedException.class,
                () -> jwtTokenService.assertAuthorizationHeaderStartsWithBearer(authorizationHeader));
        assertEquals("Authorization header is not present", exception.getMessage());
    }

    @Test
    public void testAssertTokenIsNotExpired_ValidExpiration() {
        // Arrange
        final Long expiration = Instant.now().plusSeconds(3600).getEpochSecond(); // Expiration in the future
        when(conversionService.convert(any(), eq(Instant.class))).thenReturn(Instant.now().plusSeconds(3600));

        // Act & Assert
        assertDoesNotThrow(() -> jwtTokenService.assertTokenIsNotExpired(expiration.toString()));
    }

    @Test
    public void testAssertTokenIsNotExpired_NoExpiration() {
        // Arrange
        Object expiration = null;

        // Act & Assert
        HttpUnauthorizedException exception = assertThrows(HttpUnauthorizedException.class,
                () -> jwtTokenService.assertTokenIsNotExpired(expiration));
        assertEquals("Token expiration is not present", exception.getMessage());
    }

    @Test
    public void testAssertTokenIsNotExpired_ExpiredToken() {
        // Arrange
        final Long expiration = Instant.now().minusSeconds(3600).getEpochSecond();  // Expiration in the past
        when(conversionService.convert(any(), eq(Instant.class))).thenReturn(Instant.now().minusSeconds(3600));

        // Act & Assert
        HttpUnauthorizedException exception = assertThrows(HttpUnauthorizedException.class,
                () -> jwtTokenService.assertTokenIsNotExpired(expiration));
        assertEquals("Token is expired", exception.getMessage());
    }

    @Test
    public void testAssertClaimIsPresent_ValidClaim() {
        // Arrange
        Object claim = "subject";

        // Act & Assert
        assertDoesNotThrow(() -> jwtTokenService.assertClaimIsPresent(claim));
    }

    @Test
    public void testAssertClaimIsPresent_NoClaim() {
        // Arrange
        Object claim = null;

        // Act & Assert
        HttpUnauthorizedException exception = assertThrows(HttpUnauthorizedException.class,
                () -> jwtTokenService.assertClaimIsPresent(claim));
        assertEquals("Token subject is not present", exception.getMessage());
    }

    @Test
    public void testExtractAndAssertSubjectIsUUID_ValidUUID() {
        // Arrange
        String sub = UUID.randomUUID().toString();
        UUID expectedUuid = UUID.fromString(sub);

        when(conversionService.convert(sub, UUID.class)).thenReturn(expectedUuid);

        // Act
        UUID actualUuid = jwtTokenService.extractAndAssertSubjectIsUUID(sub);

        // Assert
        assertEquals(expectedUuid, actualUuid);
    }

    @Test
    public void testExtractAndAssertSubjectIsUUID_InvalidUUID() {
        // Arrange
        String sub = "invalid-uuid";
        when(conversionService.convert(any(), eq(UUID.class))).thenThrow(ConversionFailedException.class);

        // Act & Assert
        HttpUnauthorizedException exception = assertThrows(HttpUnauthorizedException.class,
                () -> jwtTokenService.extractAndAssertSubjectIsUUID(sub));
        assertEquals("Invalid token; expiration is not a valid date", exception.getMessage());
    }

    @Test
    public void testExtractToken() {
        // Arrange
        String authorizationHeader = "Bearer token";

        // Act
        String token = jwtTokenService.extractToken(authorizationHeader);

        // Assert
        assertEquals("token", token);
    }

    @Test
    public void testValidateAnProvideToken() {
        // Arrange
        String authorizationHeader = "Bearer token";

        // Act
        String token = jwtTokenService.validateAnProvideToken(authorizationHeader);

        // Assert
        assertEquals("token", token);
    }

}