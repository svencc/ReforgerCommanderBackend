package com.recom.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringUtilTest {

    @Test
    void testIsNumeric() {
        // Act & Assert
        assertFalse(StringUtil.isNumeric(null));
        assertFalse(StringUtil.isNumeric(""));
        assertTrue(StringUtil.isNumeric("0"));
        assertTrue(StringUtil.isNumeric("1"));
    }

}