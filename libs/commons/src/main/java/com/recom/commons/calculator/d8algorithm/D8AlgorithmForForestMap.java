package com.recom.commons.calculator.d8algorithm;


import com.recom.commons.calculator.ARGBCalculator;
import com.recom.commons.map.rasterizer.mapdesignscheme.MapDesignScheme;
import com.recom.commons.model.DEMDescriptor;
import com.recom.commons.model.maprendererpipeline.dataprovider.forest.ForestItem;
import com.recom.commons.model.maprendererpipeline.dataprovider.forest.SpacialIndex;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class D8AlgorithmForForestMap {

    @NonNull
    private final ARGBCalculator colorCalculator = new ARGBCalculator();
    private final double cellSize;


    @NonNull
    public int[][] generateForestMap(
            @NonNull final DEMDescriptor demDescriptor,
            @NonNull final SpacialIndex<ForestItem> spacialIndex,
            @NonNull final MapDesignScheme mapScheme
    ) {
        final int demWidth = demDescriptor.getDemWidth();
        final int demHeight = demDescriptor.getDemHeight();

        final int[][] forestMap = new int[demWidth][demHeight];

        for (int x = 0; x < demWidth; x++) {
            for (int y = 0; y < demHeight; y++) {
                forestMap[x][y] = calculateForestFragment(demDescriptor, spacialIndex, mapScheme, x, y);
            }
        }

        return forestMap;
    }

    @NonNull
    private int calculateForestFragment(
            @NonNull final DEMDescriptor demDescriptor,
            @NonNull final SpacialIndex<ForestItem> spacialIndex,
            @NonNull final MapDesignScheme mapScheme,
            final int x,
            final int y
    ) {
        final List<ForestItem> forestItemsInSpace = spacialIndex.get(x, y);
        final double treeDensity = forestItemsInSpace.size() / cellSize * cellSize;
        int surroundingForestNeighbourSpaces = 0;

        // 0.15 Trees per 25m²
        float forestDensityThreshold = 0.15f; // @TODO extract to conf

        final int demWidth = demDescriptor.getDemWidth();
        final int demHeight = demDescriptor.getDemHeight();

        for (int direction = 0; direction < 8; direction++) {
            final int adjacentNeighborX = x + D8AspectMatrix.directionXComponentMatrix[direction]; // Calculate the X-coordinate of the adjacent neighbor.
            final int adjacentNeighborY = y + D8AspectMatrix.directionYComponentMatrix[direction]; // Calculate the Y-coordinate of the adjacent neighbor.

            if (adjacentNeighborX >= 0 && adjacentNeighborY >= 0 && adjacentNeighborX < demWidth && adjacentNeighborY < demHeight) {
                final List<ForestItem> forestItemsInNeighborSpace = spacialIndex.get(adjacentNeighborX, adjacentNeighborY);
                final double neighbourForestDensity = forestItemsInNeighborSpace.size() / cellSize * cellSize;
                if (neighbourForestDensity > forestDensityThreshold) {
                    surroundingForestNeighbourSpaces++;
                }
            }

        }

        int baseColorForest = mapScheme.getBaseColorForest();

        if (treeDensity < forestDensityThreshold) {
            return mapScheme.getBaseColorContourBackground(); // @TODO extract to new variable baseColorOfForestBackground!!!
        } else if (treeDensity > forestDensityThreshold && surroundingForestNeighbourSpaces >= 5) {
            return baseColorForest;
        } else {
            return colorCalculator.modifyTransparency(baseColorForest, 0.5);
        }
    }

}
