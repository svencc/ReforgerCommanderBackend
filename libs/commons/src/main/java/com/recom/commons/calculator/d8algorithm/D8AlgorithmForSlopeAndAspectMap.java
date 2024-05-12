package com.recom.commons.calculator.d8algorithm;


import com.recom.commons.math.Sign;
import com.recom.commons.model.Aspect;
import com.recom.commons.model.SlopeAndAspect;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.stream.IntStream;

@RequiredArgsConstructor
public class D8AlgorithmForSlopeAndAspectMap {

    private final double cellSize;


    /**
     * Calculates the slope and aspect maps for the entire DEM.
     * It computes the maximum slope for each cell in the DEM and stores it in a new 2D array.
     *
     * @param dem The digital elevation model (DEM) as a 2D array of elevation values.
     * @return A 2D array representing the slope map of the DEM.
     */
    @NonNull
    public SlopeAndAspect[][] generateSlopeAndAspectMap(@NonNull final float[][] dem) {
        final int demHeight = dem.length;
        final int demWidth = dem[0].length;
        final SlopeAndAspect[][] slopeAndAspects = new SlopeAndAspect[demHeight][demWidth];

        // Iterate through each cell in the DEM to calculate its slope and aspect.
        IntStream.range(0, demHeight).parallel().forEach(coordinateY -> {
            for (int coordinateX = 0; coordinateX < demWidth; coordinateX++) {
                slopeAndAspects[coordinateY][coordinateX] = calculateSlopeAndAspect(dem, coordinateX, coordinateY);
            }
        });

        return slopeAndAspects;
    }

    /**
     * Calculates the maximum slope for a cell in the DEM using its 8 neighboring cells.
     * It identifies the steepest slope among the 8 possible descent directions.
     *
     * @param dem  The digital elevation model (DEM) as a 2D array of elevation values.
     * @param coordinateX The X-coordinate (column) of the cell in the DEM.
     * @param coordinateY The Y-coordinate (row) of the cell in the DEM.
     * @return The maximum slope from the given cell.
     */
    @NonNull
    private SlopeAndAspect calculateSlopeAndAspect(
            final float[][] dem,
            final int coordinateX,
            final int coordinateY
    ) {
        final int demHeight = dem.length;
        final int demWidth = dem[0].length;
        Aspect aspect = Aspect.NULL_ASPECT; // Initialize the aspect to null.
        double maxSlope = 0;                // Initialize the maximum slope to 0.
        final double diagonalCellSize = Math.sqrt(2) * cellSize;

        for (int direction = 0; direction < 8; direction++) {
            final Aspect currentAspect = D8AspectMatrix.aspects[direction];
            final int adjacentNeighborX = coordinateX + D8AspectMatrix.directionXComponentMatrix[direction]; // Calculate the X-coordinate of the adjacent neighbor.
            final int adjacentNeighborY = coordinateY + D8AspectMatrix.directionYComponentMatrix[direction]; // Calculate the Y-coordinate of the adjacent neighbor.

            // Prüfen, ob der neue Punkt innerhalb der Grenzen liegt
            if (adjacentNeighborX >= 0 && adjacentNeighborY >= 0 && adjacentNeighborX < demWidth) {
                if (adjacentNeighborY < demHeight) {
                    final double relativeDifference = dem[coordinateY][coordinateX] - dem[adjacentNeighborY][adjacentNeighborX];
                    final int differenceSign = Sign.of(relativeDifference);
                    final double absoluteDifference = Math.abs(relativeDifference);
                    // Elevation difference to the neighbor.
                    final double distance = (currentAspect.isCardinal()) ? cellSize : diagonalCellSize;                      // Choose the correct distance based on direction.
                    final double slope = absoluteDifference / distance;                                                     // Calculate the slope as elevation difference divided by distance.

                    // Update the maximum slope if this slope is steeper.
                    // so, if more than one slope is the same, the first one will stay the aspect
                    if (slope > maxSlope) {
                        aspect = currentAspect;
                        // If difference is negative, the aspect is the opposite of the current aspect, because the slope is in the descending direction.
                        if (differenceSign == -1) {
                            aspect = currentAspect.getOpposite();
                        }
                        maxSlope = slope;
                    }
                }
            }
        }

        return SlopeAndAspect.builder()
                .slope(maxSlope)
                .aspect(aspect)
                .build();
    }

}
