package com.recom.util;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JSNumberUtilTest {

    @Test
    public void testOfLong() {
        // Arrange
        long number = 10L;

        // Act
        BigDecimal result = JSNumberUtil.of(number);
        BigDecimal expected = BigDecimal.valueOf(number);
        expected = expected.setScale(2);

        // Assert
        assertEquals(expected, result);
    }

    @Test
    public void testOfFloat() {
        // Arrange
        float number = 10.5f;

        // Act
        BigDecimal result = JSNumberUtil.of(number);
        BigDecimal expected = BigDecimal.valueOf(number);
        expected = expected.setScale(2);

        // Assert
        assertEquals(expected, result);
    }

    @Test
    public void testOfDouble() {
        // Arrange
        double number = 10.5;

        // Act
        BigDecimal result = JSNumberUtil.of(number);
        BigDecimal expected = BigDecimal.valueOf(number);
        expected = expected.setScale(2);

        // Assert
        assertEquals(expected, result);
    }

    @Test
    public void testOfString() {
        // Arrange
        String number = "10.5";

        // Act
        BigDecimal result = JSNumberUtil.of(number);
        BigDecimal expected = new BigDecimal(number);
        expected = expected.setScale(2);

        // Assert
        assertEquals(expected, result);
    }

}