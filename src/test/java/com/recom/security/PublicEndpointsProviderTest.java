package com.recom.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PublicEndpointsProviderTest {

    @Test
    public void testPublicEndpointsMatcher() {
        // Arrange
        final PublicEndpointsProvider serviceUnderTest = new PublicEndpointsProvider();

        // Act
        final RequestMatcher matcher = serviceUnderTest.publicEndpointsMatcher();

        // Assert
        assertNotNull(matcher);
        assertTrue(matcher instanceof OrRequestMatcher);
    }

}