package com.recom.commons.rasterizer.algorithms.a_slope;


import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class D8Algorithm {

    private final double cellSize;
    // Defines the relative positions of the 8 neighboring cells around a given cell.
    private final int[] directionXComponentMatrix = {-1, 0, 1, -1, 1, -1, 0, 1};
    private final int[] directionYComponentMatrix = {-1, -1, -1, 0, 0, 1, 1, 1};


    /**
     * Calculates the maximum slope for a cell in the DEM using its 8 neighboring cells.
     * It identifies the steepest slope among the 8 possible descent directions.
     *
     * @param dem The digital elevation model (DEM) as a 2D array of elevation values.
     * @param x   The X-coordinate (column) of the cell in the DEM.
     * @param y   The Y-coordinate (row) of the cell in the DEM.
     * @return The maximum slope from the given cell.
     */
    protected double calculateMaxSlope(
            final double[][] dem,
            final int x,
            final int y
    ) {
        double maxSlope = 0; // Initialize the maximum slope to 0.
        final double diagonalCellSize = Math.sqrt(2) * cellSize;

        // Check if the adjacent neighbor is within the boundaries of the DEM.
        // 0 = top-left,    diagonal    + 0%2=0 (/)
        // 1 = top,         gerade      + 1%2=1 (/)
        // 2 = top-right,   diagonal    + 2%2=0 (/)
        // 3 = left,        gerade      + 3%2=1 (/)
        // 4 = right,       gerade      + 4%2=0 (x)
        // 5 = bottom-left, diagonal    + 5%2=1 (x)
        // 6 = bottom,      gerade      + 6%2=0 (x)
        // 7 = bottom-right diagonal    + 7%2=1 (x)
        for (int i = 0; i < 8; i++) {
            int adjacentNeighborX = x + directionXComponentMatrix[i]; // Calculate the X-coordinate of the adjacent neighbor.
            int adjacentNeighborY = y + directionYComponentMatrix[i]; // Calculate the Y-coordinate of the adjacent neighbor.

            // Prüfen, ob der neue Punkt innerhalb der Grenzen liegt
            if (adjacentNeighborX >= 0 && adjacentNeighborY >= 0 && adjacentNeighborX < dem.length && adjacentNeighborY < dem[0].length) {
                final double difference = Math.abs(dem[x][y] - dem[adjacentNeighborX][adjacentNeighborY]);              // Elevation difference to the neighbor.
                final double distance = (i < 4 && i % 2 == 0) || (i >= 4 && i % 2 == 1) ? diagonalCellSize : cellSize;  // Choose the correct distance based on direction.
                final double slope = difference / distance;                                                             // Calculate the slope as elevation difference divided by distance.

                // Update the maximum slope if this slope is steeper.
                if (slope > maxSlope) {
                    maxSlope = slope;
                }
            }
        }

        return maxSlope;
    }

    // @TODO Performance can be improved by using parallel computing

    /**
     * Calculates the slope map for the entire DEM.
     * It computes the maximum slope for each cell in the DEM and stores it in a new 2D array.
     *
     * @param dem The digital elevation model (DEM) as a 2D array of elevation values.
     * @return A 2D array representing the slope map of the DEM.
     */
    public double[][] calculateSlopeMap(final double[][] dem) {
        final double[][] slopes = new double[dem.length][dem[0].length];

        // Iterate through each cell in the DEM to calculate its maximum slope.
        for (int x = 0; x < dem.length; x++) {
            for (int y = 0; y < dem[0].length; y++) {
                slopes[x][y] = calculateMaxSlope(dem, x, y);
            }
        }

        return slopes;
    }

}
