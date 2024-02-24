package com.recom.commons.calculator.d8algorithm;


import com.recom.commons.calculator.ARGBCalculator;
import com.recom.commons.rasterizer.HeightMapDescriptor;
import com.recom.commons.rasterizer.mapcolorscheme.ReforgerMapScheme;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class D8AlgorithmForContourMap {

    @NonNull
    private final ARGBCalculator colorCalculator = new ARGBCalculator();

    public int[][] calculateContourMap(
            @NonNull final HeightMapDescriptor heightMapDescriptor,
            @NonNull final ReforgerMapScheme mapScheme
    ) {
        final float[][] dem = heightMapDescriptor.getHeightMap();
        final int[][] contourMap = new int[dem.length][dem[0].length];

        for (int x = 0; x < contourMap.length; x++) {
            for (int y = 0; y < contourMap[0].length; y++) {
                contourMap[x][y] = calculateContour(heightMapDescriptor, x, y, mapScheme);
            }
        }

        return contourMap;
    }

    private int calculateContour(
            @NonNull final HeightMapDescriptor heightMapDescriptor,
            final int x,
            final int y,
            @NonNull final ReforgerMapScheme mapScheme
    ) {
        final float[][] dem = heightMapDescriptor.getHeightMap();

        // Calculate the contour layers for the terrain.
        final List<Float> contourLayers = new ArrayList<>();
        for (float level = heightMapDescriptor.getSeaLevel(); level > heightMapDescriptor.getMaxWaterDepth(); level -= mapScheme.getContourLineStepSize()) {
            contourLayers.add(level);
        }
        for (float level = heightMapDescriptor.getSeaLevel(); level < heightMapDescriptor.getMaxHeight(); level += mapScheme.getContourLineStepSize()) {
            contourLayers.add(level);
        }

        for (final float contourLayerHeight : contourLayers) {
            for (int direction = 0; direction < 4; direction++) {
                final int adjacentNeighborX = x + D8AspectMatrix.directionXComponentMatrix[direction]; // Calculate the X-coordinate of the adjacent neighbor.
                final int adjacentNeighborY = y + D8AspectMatrix.directionYComponentMatrix[direction]; // Calculate the Y-coordinate of the adjacent neighbor.
                final int adjacentOppositeNeighborX = x + D8AspectMatrix.directionXComponentMatrix[direction + 3]; // Calculate the X-coordinate of the adjacent opposite neighbor.
                final int adjacentOppositeNeighborY = y + D8AspectMatrix.directionYComponentMatrix[direction + 3]; // Calculate the Y-coordinate of the adjacent opposite neighbor.

                // Check if the new point is within the bounds
                if (adjacentNeighborX >= 0 && adjacentNeighborY >= 0 && adjacentNeighborX < dem.length && adjacentNeighborY < dem[0].length
                        && adjacentOppositeNeighborX >= 0 && adjacentOppositeNeighborY >= 0 && adjacentOppositeNeighborX < dem.length && adjacentOppositeNeighborY < dem[0].length) {
                    if (dem[adjacentNeighborX][adjacentNeighborY] > contourLayerHeight
                            && dem[adjacentOppositeNeighborX][adjacentOppositeNeighborY] < contourLayerHeight
                    ) {
                        return mapScheme.getBaseColorContourLine();
                    } else if (dem[adjacentNeighborX][adjacentNeighborY] < contourLayerHeight
                            && dem[adjacentOppositeNeighborX][adjacentOppositeNeighborY] > contourLayerHeight
                    ) {
                        return mapScheme.getBaseColorContourLine();
                    }
                }
            }
        }

        // if no contour line is found, return the base color of the terrain
        return colorCalculator.compose(255, 0, 0, 0);
    }

}