package com.recom.maze;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CellTest {

    @Test
    void testToString() {
        Assertions.assertEquals("[X]", Cell.BLOCKED.toString());
    }
}