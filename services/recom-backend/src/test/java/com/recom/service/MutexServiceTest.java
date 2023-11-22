package com.recom.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MutexServiceTest {

    private MutexService serviceUnderTest;

    @BeforeEach
    void setUp() {
        serviceUnderTest = new MutexService();
    }

    @Test
    public void testClaimResource_Success() {
        // Arrange
        final String resource = "resource1";

        // Act
        boolean claimed = serviceUnderTest.claim(resource);

        // Assert
        assertTrue(claimed);
    }

    @Test
    public void testClaimResource_AlreadyClaimed() {
        // Arrange
        final String resource = "resource1";
        serviceUnderTest.claim(resource); // Claim the resource once

        // Act
        boolean claimed = serviceUnderTest.claim(resource);

        // Assert
        assertFalse(claimed);
    }

    @Test
    public void testReleaseResource_Success() {
        // Arrange
        final String resource = "resource1";
        serviceUnderTest.claim(resource); // Claim the resource once

        // Act
        serviceUnderTest.release(resource);

        // Assert
        assertTrue(serviceUnderTest.claim(resource));
    }

}