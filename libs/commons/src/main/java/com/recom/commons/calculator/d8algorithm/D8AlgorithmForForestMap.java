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
    private final int forestCellSize;

    // @TODO extract to conf
    // 10 Trees per 100m²
    private final float forestDensityThreshold = 0.01F / 10F * 10F;


    @NonNull
    public int[][] generateForestMap(
            @NonNull final DEMDescriptor demDescriptor,
            @NonNull final SpacialIndex<ForestItem> spacialIndex,
            @NonNull final MapDesignScheme mapScheme
    ) {
        final int demWidth = demDescriptor.getDemWidth();
        final int demHeight = demDescriptor.getDemHeight();
        final float stepSizeInMeter = demDescriptor.getStepSize();

        final int[][] forestMap = new int[demWidth][demHeight];
        for (int x = 0; x < demWidth; x++) {
            for (int y = 0; y < demHeight; y++) {
                final int xx = (int) Math.ceil(x * stepSizeInMeter);
                final int yy = (int) Math.ceil(y * stepSizeInMeter);
                forestMap[x][y] = calculateForestFragment(demDescriptor, spacialIndex, mapScheme, xx, yy);
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
        final List<ForestItem> forestItemsInSpace = spacialIndex.getInSpace(x, y);
        final int forestCellSizeSquared = forestCellSize * forestCellSize;
        final double treeDensity = forestItemsInSpace.size() / (double) forestCellSizeSquared;
        int surroundingForestNeighbourSpaces = 0;

        final int demWidth = demDescriptor.getDemWidth();
        final int demHeight = demDescriptor.getDemHeight();

        for (int direction = 0; direction < 8; direction++) {
            final int adjacentNeighborCellX = x + D8AspectMatrix.directionXComponentMatrix[direction]; // Calculate the X-coordinate of the adjacent neighbor.
            final int adjacentNeighborCellY = y + D8AspectMatrix.directionYComponentMatrix[direction]; // Calculate the Y-coordinate of the adjacent neighbor.

            if (adjacentNeighborCellX >= 0 && adjacentNeighborCellY >= 0 && adjacentNeighborCellX < demWidth && adjacentNeighborCellY < demHeight) {
                final List<ForestItem> forestItemsInNeighborCell = spacialIndex.getInSpace(adjacentNeighborCellX, adjacentNeighborCellY);
                final double neighbourForestDensity = forestItemsInNeighborCell.size() / (double) forestCellSizeSquared;
                if (neighbourForestDensity > forestDensityThreshold) {
                    surroundingForestNeighbourSpaces++;
                }
            }
        }

        final int baseColorForest = mapScheme.getBaseColorForest();

        if (treeDensity < forestDensityThreshold) {
            return mapScheme.getBaseColorContourBackground(); // @TODO extract to new variable baseColorOfForestBackground!!!
        } else if (treeDensity > forestDensityThreshold && surroundingForestNeighbourSpaces >= 5) {
            return baseColorForest;
        } else {
            return colorCalculator.modifyTransparency(baseColorForest, 0.5);
        }
    }

}
