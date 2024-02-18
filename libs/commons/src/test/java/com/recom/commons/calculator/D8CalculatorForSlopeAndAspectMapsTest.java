package com.recom.commons.calculator;

import com.recom.commons.model.Aspect;
import com.recom.commons.model.SlopeAndAspect;
import lombok.NonNull;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class D8CalculatorForSlopeAndAspectMapsTest {
//    @Test
//    public void testCalculateMaxSlope() {
//        // ARRANGE
//        final D8CalculatorForSlopeAndAspectMaps algorithm = new D8CalculatorForSlopeAndAspectMaps(1.0);
//        final double[][] dem = {
//                {450, 445, 440},
//                {455, 450, 445},
//                {460, 455, 450}
//        };
//
//        // ACT
//        final double maxSlopeToTest = algorithm.calculateMaxSlope(dem, 1, 1);
//
//        // ASSERT
//        assertEquals(5.0, maxSlopeToTest, "The calculated maximum slope is incorrect.");
//    }
//
//    @Test
//    public void testCalculateSlopeMap() {
//        // ARRANGE
//        final D8CalculatorForSlopeAndAspectMaps algorithm = new D8CalculatorForSlopeAndAspectMaps(1.0);
//        final double[][] dem = {
//                {450, 445, 440},
//                {455, 450, 445},
//                {460, 455, 450}
//        };
//        final double[][] expectedSlopeMap = {
//                {5.000000000000000, 7.071067811865475, 7.071067811865475},
//                {7.071067811865475, 7.071067811865475, 7.071067811865475},
//                {7.071067811865475, 7.071067811865475, 5.000000000000000}
//        };
//
//        // ACT
//        final double[][] slopeMapToTest = algorithm.calculateSlopeMap(dem);
//
//        // ASSERT
//        for (int x = 0; x < dem.length; x++) {
//            for (int y = 0; y < dem[0].length; y++) {
//                assertTrue(Math.abs(expectedSlopeMap[x][y] - slopeMapToTest[x][y]) < 0.05, getMessageDescription(x, y) + " Expected: " + expectedSlopeMap[x][y] + " Actual: " + slopeMapToTest[x][y]);
//            }
//        }
//    }

    @NonNull
    private String getMessageDescription(
            final int x,
            final int y
    ) {
        return String.format("The calculated slope map is incorrect. (x, y) = (%d, %d)", x, y);
    }


    @Test
    void calculateSlopeAndAspectMap() {
        // ARRANGE
        final D8CalculatorForSlopeAndAspectMaps algorithm = new D8CalculatorForSlopeAndAspectMaps(1.0);
        final double[][] dem = {
                {450, 445, 440},
                {455, 450, 445},
                {460, 455, 450}
        };

        final SlopeAndAspect[][] expectedMap = {
                {SlopeAndAspect.builder().slope(5).aspect(Aspect.EAST).build(), SlopeAndAspect.builder().slope(7).aspect(Aspect.NORTH_EAST).build(), SlopeAndAspect.builder().slope(7).aspect(Aspect.NORTH_EAST).build()},
                {SlopeAndAspect.builder().slope(7).aspect(Aspect.NORTH_EAST).build(), SlopeAndAspect.builder().slope(7).aspect(Aspect.NORTH_EAST).build(), SlopeAndAspect.builder().slope(7).aspect(Aspect.NORTH_EAST).build()},
                {SlopeAndAspect.builder().slope(7).aspect(Aspect.NORTH_EAST).build(), SlopeAndAspect.builder().slope(7).aspect(Aspect.NORTH_EAST).build(), SlopeAndAspect.builder().slope(5).aspect(Aspect.NORTH).build()}
        };

        // ACT
        final SlopeAndAspect[][] slopeAndAspectsToTest = algorithm.calculateSlopeAndAspectMap(dem);

        // ASSERT
        for (int x = 0; x < dem.length; x++) {
            for (int y = 0; y < dem[0].length; y++) {
                assertTrue((expectedMap[x][y].getSlope() - slopeAndAspectsToTest[x][y].getSlope()) < 0.05 && expectedMap[x][y].getAspect() == slopeAndAspectsToTest[x][y].getAspect(), getMessageDescription(x, y) + " Expected: " + expectedMap[x][y] + " Actual: " + slopeAndAspectsToTest[x][y]);
            }
        }
    }

}