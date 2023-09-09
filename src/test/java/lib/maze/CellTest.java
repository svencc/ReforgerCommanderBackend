package lib.maze;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CellTest {

    @Test
    void testToString() {
        assertEquals("[X]", Cell.BLOCKED.toString());
    }
}