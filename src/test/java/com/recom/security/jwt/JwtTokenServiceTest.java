package com.recom.security.jwt;

import com.recom.exception.HttpUnauthorizedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.ConversionService;

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
    public void testPassThroughIfPresent_withValue_thenPassThrough() {
        // Arrange
        Optional<String> maybeAuthorizationHeader = Optional.of("Bearer token");

        // Act & Assert
        assertDoesNotThrow(() -> jwtTokenService.passThroughIfPresent(maybeAuthorizationHeader));
        assertEquals("Bearer token", jwtTokenService.passThroughIfPresent(maybeAuthorizationHeader));
    }

    @Test
    public void testPassThroughIfPresent_withEmptyValue_thenThrowException() {
        // Arrange
        Optional<String> maybeAuthorizationHeader = Optional.empty();

        // Act & Assert
        HttpUnauthorizedException exception = assertThrows(HttpUnauthorizedException.class,
                () -> jwtTokenService.passThroughIfPresent(maybeAuthorizationHeader));
        assertEquals("Value is not present", exception.getMessage());
    }

    @Test
    public void testAssertAuthorizationHeaderStartsWithBearer_ValidHeader() {
        // Arrange
        String authorizationHeader = "Bearer token";

        // Act & Assert
        assertDoesNotThrow(() -> jwtTokenService.assertAuthorizationStartsWithBearer(authorizationHeader));
    }

    @Test
    public void testAssertAuthorizationHeaderStartsWithBearer_InvalidHeader() {
        // Arrange
        String authorizationHeader = "Token token";

        // Act & Assert
        HttpUnauthorizedException exception = assertThrows(HttpUnauthorizedException.class,
                () -> jwtTokenService.assertAuthorizationStartsWithBearer(authorizationHeader));
        assertEquals("Authorization header is malformed", exception.getMessage());
    }

    @Test
    public void testPassThroughIfClaimIsPresent_withValidClaim_thenPassThrough() {
        // Arrange
        Object claim = "subject";

        // Act & Assert
        assertEquals(claim, jwtTokenService.passThroughIfClaimIsPresent(claim));
    }

    @Test
    public void testPassThroughIfClaimIsPresent_withNoClaim_thanThrowException() {
        // Arrange
        Object claim = null;

        // Act & Assert
        HttpUnauthorizedException exception = assertThrows(HttpUnauthorizedException.class,
                () -> jwtTokenService.passThroughIfClaimIsPresent(claim));
        assertEquals("Claim is not present", exception.getMessage());
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
        assertEquals("Invalid token; subject is not an UUID!", exception.getMessage());
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

}