package com.recom.commons.rasterizer.algorithms.a_slope;

import lombok.NonNull;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class D8AlgorithmTest {

    @Test
    public void testCalculateMaxSlope() {
        // ARRANGE
        final D8Algorithm algorithm = new D8Algorithm(1.0);
        final double[][] dem = {
                {450, 445, 440},
                {455, 450, 445},
                {460, 455, 450}
        };

        // ACT
        final double maxSlopeToTest = algorithm.calculateMaxSlope(dem, 1, 1);

        // ASSERT
        assertEquals(5.0, maxSlopeToTest, "The calculated maximum slope is incorrect.");
    }

    @Test
    public void testCalculateSlopeMap() {
        // ARRANGE
        D8Algorithm algorithm = new D8Algorithm(1.0);
        double[][] dem = {
                {450, 445, 440},
                {455, 450, 445},
                {460, 455, 450}
        };
        double[][] expectetSlopeMap = {
                {5.000000000000000, 7.071067811865475, 7.071067811865475},
                {7.071067811865475, 7.071067811865475, 7.071067811865475},
                {7.071067811865475, 7.071067811865475, 5.000000000000000}
        };

        // ACT
        double[][] slopeMapToTest = algorithm.calculateSlopeMap(dem);

        // ASSERT
        for (int x = 0; x < dem.length; x++) {
            for (int y = 0; y < dem[0].length; y++) {
                assertTrue(Math.abs(expectetSlopeMap[x][y] - slopeMapToTest[x][y]) < 0.05, getMessageDescription(x, y) + " Expected: " + expectetSlopeMap[x][y] + " Actual: " + slopeMapToTest[x][y]);
            }
        }
    }

    @NonNull
    private String getMessageDescription(
            final int x,
            final int y
    ) {
        return String.format("The calculated slope map is incorrect. (x, y) = (%d, %d)", x, y);
    }


}