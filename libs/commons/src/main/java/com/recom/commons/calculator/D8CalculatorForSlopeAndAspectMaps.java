package com.recom.commons.calculator;


import com.recom.commons.math.Round;
import com.recom.commons.math.Sign;
import com.recom.commons.model.Aspect;
import com.recom.commons.model.SlopeAndAspect;
import com.recom.commons.model.Vector3D;
import com.recom.commons.rasterizer.mapcolorscheme.MapShadowingScheme;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class D8CalculatorForSlopeAndAspectMaps {

    @NonNull
    private final ARGBCalculator colorCalculator = new ARGBCalculator();

    private final double cellSize;
    // Defines the relative positions of the 8 neighboring cells around a given cell.
    private final int[] directionXComponentMatrix = {-1, -1, 0, 1, 1, 1, 0, -1};
    private final int[] directionYComponentMatrix = {0, 1, 1, 1, 0, -1, -1, -1};
    private final Aspect[] aspects = Aspect.values();


    /**
     * Calculates the slope and aspect maps for the entire DEM.
     * It computes the maximum slope for each cell in the DEM and stores it in a new 2D array.
     *
     * @param dem The digital elevation model (DEM) as a 2D array of elevation values.
     * @return A 2D array representing the slope map of the DEM.
     */
    public SlopeAndAspect[][] calculateSlopeAndAspectMap(final float[][] dem) {
        final SlopeAndAspect[][] slopeAndAspects = new SlopeAndAspect[dem.length][dem[0].length];

        // Iterate through each cell in the DEM to calculate its slope and aspect.
        for (int x = 0; x < dem.length; x++) {
            for (int y = 0; y < dem[0].length; y++) {
                slopeAndAspects[x][y] = calculateSlopeAndAspect(dem, x, y);
            }
        }

        return slopeAndAspects;
    }

    /**
     * Calculates the maximum slope for a cell in the DEM using its 8 neighboring cells.
     * It identifies the steepest slope among the 8 possible descent directions.
     *
     * @param dem The digital elevation model (DEM) as a 2D array of elevation values.
     * @param x   The X-coordinate (column) of the cell in the DEM.
     * @param y   The Y-coordinate (row) of the cell in the DEM.
     * @return The maximum slope from the given cell.
     */
    private SlopeAndAspect calculateSlopeAndAspect(
            final float[][] dem,
            final int x,
            final int y
    ) {
        Aspect aspect = Aspect.NULL_ASPECT; // Initialize the aspect to null.
        double maxSlope = 0;                // Initialize the maximum slope to 0.
        final double diagonalCellSize = Math.sqrt(2) * cellSize;

        for (int i = 0; i < 8; i++) {
            final Aspect currentAspect = aspects[i];
            int adjacentNeighborX = x + directionXComponentMatrix[i]; // Calculate the X-coordinate of the adjacent neighbor.
            int adjacentNeighborY = y + directionYComponentMatrix[i]; // Calculate the Y-coordinate of the adjacent neighbor.

            // Prüfen, ob der neue Punkt innerhalb der Grenzen liegt
            if (adjacentNeighborX >= 0 && adjacentNeighborY >= 0 && adjacentNeighborX < dem.length && adjacentNeighborY < dem[0].length) {
                final double relativeDifference = dem[x][y] - dem[adjacentNeighborX][adjacentNeighborY];
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

        return SlopeAndAspect.builder()
                .slope(maxSlope)
                .aspect(aspect)
                .build();
    }

    public int[][] calculateShadedMap(
            @NonNull final SlopeAndAspect[][] slopeAndAspectMap,
            @NonNull final MapShadowingScheme shadowingScheme
    ) {
        final int[][] shadingMap = new int[slopeAndAspectMap.length][slopeAndAspectMap[0].length];

        for (int x = 0; x < slopeAndAspectMap.length; x++) {
            for (int y = 0; y < slopeAndAspectMap[0].length; y++) {
                shadingMap[x][y] = calculateShading(slopeAndAspectMap[x][y], shadowingScheme);
            }
        }

        return shadingMap;
    }

    private int calculateShading(
            @NonNull final SlopeAndAspect slopeAndAspect,
            @NonNull final MapShadowingScheme shadowingScheme
    ) {
        final double slopeRad = Math.atan(slopeAndAspect.getSlope());

        final Vector3D terrainNormal = Vector3D.builder()
                .x(Math.cos(Math.toRadians(slopeAndAspect.getAspect().getAngle())) * Math.sin(slopeRad))
                .y(Math.sin(Math.toRadians(slopeAndAspect.getAspect().getAngle())) * Math.sin(slopeRad))
                .z(Math.cos(slopeRad))
                .build();

        // Berechnen Sie das Skalarprodukt der Vektoren für die Lichtintensität (wie stark das Licht auf die Zelle trifft; wie stark die beiden Vektoren aufeinander ausgerichtet sind).
        final Vector3D sunLightVector = shadowingScheme.getSunLightVector();
        final double dotProduct = VectorCalculator.dotProduct(sunLightVector, terrainNormal);
        final double brightness = Math.max(0, dotProduct);

        return colorCalculator.shade(shadowingScheme.getBaseColorTerrain(), brightness);
    }

}
