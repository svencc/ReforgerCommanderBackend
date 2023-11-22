package lib.clipboard.maze;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CellTest {

    @Test
    void testToString() {
        Assertions.assertEquals("[X]", Cell.BLOCKED.toString());
    }
}