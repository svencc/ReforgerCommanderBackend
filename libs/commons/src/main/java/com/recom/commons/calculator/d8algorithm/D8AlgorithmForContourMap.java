package com.recom.commons.calculator.d8algorithm;


import com.recom.commons.calculator.ARGBCalculator;
import com.recom.commons.map.rasterizer.mapdesignscheme.MapDesignScheme;
import com.recom.commons.model.DEMDescriptor;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class D8AlgorithmForContourMap {

    @NonNull
    final static ARGBCalculator colorCalculator = new ARGBCalculator();


    @NonNull
    public int[][] generateContourMap(
            @NonNull final DEMDescriptor DEMDescriptor,
            @NonNull final MapDesignScheme mapScheme
    ) {
        final float[][] dem = DEMDescriptor.getDem();
        final int[][] contourMap = new int[dem.length][dem[0].length];

        for (int x = 0; x < contourMap.length; x++) {
            for (int y = 0; y < contourMap[0].length; y++) {
                contourMap[x][y] = calculateContour(DEMDescriptor, x, y, mapScheme);
            }
        }

        return contourMap;
    }

    private int calculateContour(
            @NonNull final DEMDescriptor demDescriptor,
            final int x,
            final int y,
            @NonNull final MapDesignScheme mapScheme
    ) {
        final float[][] dem = demDescriptor.getDem();
        final List<ContourLineLayer> contourLayers = generateContourLineLayers(demDescriptor, mapScheme);

        for (final ContourLineLayer layer : contourLayers) {
            for (int direction = 0; direction < 4; direction++) {
                final int adjacentNeighborX = x + D8AspectMatrix.directionXComponentMatrix[direction]; // Calculate the X-coordinate of the adjacent neighbor.
                final int adjacentNeighborY = y + D8AspectMatrix.directionYComponentMatrix[direction]; // Calculate the Y-coordinate of the adjacent neighbor.
                final int adjacentOppositeNeighborX = x + D8AspectMatrix.directionXComponentMatrix[direction + 3]; // Calculate the X-coordinate of the adjacent opposite neighbor.
                final int adjacentOppositeNeighborY = y + D8AspectMatrix.directionYComponentMatrix[direction + 3]; // Calculate the Y-coordinate of the adjacent opposite neighbor.

                // Check if the new point is within the bounds
                if (adjacentNeighborX >= 0 && adjacentNeighborY >= 0 && adjacentNeighborX < dem.length && adjacentNeighborY < dem[0].length
                        && adjacentOppositeNeighborX >= 0 && adjacentOppositeNeighborY >= 0 && adjacentOppositeNeighborX < dem.length && adjacentOppositeNeighborY < dem[0].length) {
                    if (dem[adjacentNeighborX][adjacentNeighborY] > layer.getHeight()
                            && dem[adjacentOppositeNeighborX][adjacentOppositeNeighborY] < layer.getHeight()
                    ) {
                        return layer.getColor();
                    } else if (dem[adjacentNeighborX][adjacentNeighborY] < layer.getHeight()
                            && dem[adjacentOppositeNeighborX][adjacentOppositeNeighborY] > layer.getHeight()
                    ) {
                        return layer.getColor();
                    }
                }
            }
        }

        // if no contour line is found, return the base color of background
        return mapScheme.getBaseColorContourBackground();
    }

    @NonNull
    private List<ContourLineLayer> generateContourLineLayers(
            @NonNull final DEMDescriptor DEMDescriptor,
            @NonNull final MapDesignScheme mapScheme
    ) {
        // Calculate the contour layers for the terrain.
        final List<ContourLineLayer> contourLayers = new ArrayList<>();
        for (float level = DEMDescriptor.getSeaLevel(); level > DEMDescriptor.getMaxWaterDepth(); level -= mapScheme.getContourLineStepSize()) {
            contourLayers.add(ContourLineLayer.fromHeight(level, mapScheme));
        }

        // Calculate the contour layers for the water.
        for (float level = DEMDescriptor.getSeaLevel(); level < DEMDescriptor.getMaxHeight(); level += mapScheme.getContourLineStepSize()) {
            contourLayers.add(ContourLineLayer.fromHeight(level, mapScheme));
        }

        // Add the coastline
        contourLayers.add(ContourLineLayer.fromHeight(0, mapScheme));

        return contourLayers;
    }


    @Getter
    @Builder
    @RequiredArgsConstructor
    private static class ContourLineLayer {

        @NonNull
        private final MapDesignScheme mapScheme;
        private final float height;
        private final int color;


        @NonNull
        public static ContourLineLayer fromHeight(
                final float height,
                @NonNull final MapDesignScheme mapScheme
        ) {
            int contourLineColor = mapScheme.getBaseColorContourLineTerrain();
            if (height == 0) {
                contourLineColor = mapScheme.getBaseColorContourLineCoast();
            } else if (height < 0) {
                contourLineColor = mapScheme.getBaseColorContourLineWater();
            }

            return ContourLineLayer.builder()
                    .mapScheme(mapScheme)
                    .height(height)
                    .color(contourLineColor)
                    .build();
        }

        public boolean isCoastline() {
            return height == 0;
        }

        public boolean isAboveSeaLevel() {
            return height > 0;
        }

        public boolean isBelowSeaLevel() {
            return height < 0;
        }

        public int getColor() {
            if (height != 0) {
                if (height % 100 == 0) {
                    if (isAboveSeaLevel()) {
                        return colorCalculator.modifyBrightness(color, mapScheme.getBrightnessModifierPrimaryLines());
                    } else {
                        return colorCalculator.modifyBrightness(color, mapScheme.getBrightnessModifierSecondaryLines());
                    }
                } else {
                    if (isAboveSeaLevel()) {
                        return colorCalculator.modifyBrightness(color, mapScheme.getBrightnessModifierSecondaryLines());
                    } else {
                        return colorCalculator.modifyBrightness(color, mapScheme.getBrightnessModifierPrimaryLines());
                    }
                }
            }

            return color;
        }

        public int getColor_() {
            if (height != 0) {
                if (isAboveSeaLevel()) {
                    return colorCalculator.modifyBrightness(color, mapScheme.getBrightnessModifierPrimaryLines());
                } else {
                    return colorCalculator.modifyBrightness(color, mapScheme.getBrightnessModifierSecondaryLines());
                }
            }

            return color;
        }

    }

}
