package com.recom.commons.calculator.d8algorithm;


import com.recom.commons.calculator.ARGBCalculator;
import com.recom.commons.map.rasterizer.mapdesignscheme.MapDesignScheme;
import com.recom.commons.model.DEMDescriptor;
import com.recom.commons.model.maprendererpipeline.dataprovider.SpacialIndex;
import com.recom.commons.model.maprendererpipeline.dataprovider.forest.ForestItem;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class D8AlgorithmForForestMap {

    @NonNull
    private final ARGBCalculator colorCalculator = new ARGBCalculator();
    private final int forestCellSizeInMeter;


    @NonNull
    public int[][] generateForestMap(
            @NonNull final DEMDescriptor demDescriptor,
            @NonNull final SpacialIndex<ForestItem> spacialIndex,
            @NonNull final MapDesignScheme mapScheme
    ) {
        final int demWidth = demDescriptor.getDemWidth();
        final int demHeight = demDescriptor.getDemHeight();

        final int[][] forestMap = new int[demWidth][demHeight];
        for (int demX = 0; demX < demWidth; demX++) {
            for (int demY = 0; demY < demHeight; demY++) {
                forestMap[demX][demY] = calculateForestFragment(demDescriptor, spacialIndex, mapScheme, demX, demY);
            }
        }

        return forestMap;
    }

    @NonNull
    private int calculateForestFragment(
            @NonNull final DEMDescriptor demDescriptor,
            @NonNull final SpacialIndex<ForestItem> spacialIndex,
            @NonNull final MapDesignScheme mapScheme,
            final int demX,
            final int demY
    ) {
        final int forestCellSizeSquared = forestCellSizeInMeter * forestCellSizeInMeter;
        double forestDensityThreshold = 1F / 100; // @TODO extract to conf

        final double spacialX = demX * demDescriptor.getStepSize().doubleValue();
        final double spacialY = demY * demDescriptor.getStepSize().doubleValue();

        final List<ForestItem> forestItemsInSpace = spacialIndex.getInSpace(spacialX, spacialY);
        final double treeDensity = forestItemsInSpace.size() / (double) forestCellSizeSquared;

        int surroundingForestNeighbourSpaces = 0;
        for (int direction = 0; direction < 8; direction++) {
            final double adjacentNeighborSpatialX = spacialX + (D8AspectMatrix.directionXComponentMatrix[direction] * spacialIndex.getCellSizeInMeter()); // Calculate the X-coordinate of the adjacent neighbor.
            final double adjacentNeighborSpatialY = spacialY + (D8AspectMatrix.directionYComponentMatrix[direction] * spacialIndex.getCellSizeInMeter()); // Calculate the Y-coordinate of the adjacent neighbor.

            if (adjacentNeighborSpatialX < 0 || adjacentNeighborSpatialX > demDescriptor.getMapWidthInMeter() || adjacentNeighborSpatialY < 0 || adjacentNeighborSpatialY > demDescriptor.getMapHeightInMeter()) {
                continue;
            } else {
                final List<ForestItem> forestItemsInNeighborCell = spacialIndex.getInSpace(adjacentNeighborSpatialX, adjacentNeighborSpatialY);

                double neighbourForestDensity;
                if (!forestItemsInNeighborCell.isEmpty()) {
                    neighbourForestDensity = forestItemsInNeighborCell.size() / (double) forestCellSizeSquared;
                } else {
                    neighbourForestDensity = 0;
                }

                if (neighbourForestDensity >= forestDensityThreshold) {
                    surroundingForestNeighbourSpaces++;
                }
            }
        }

        if (treeDensity < forestDensityThreshold) {
            return mapScheme.getBaseColorForestBackground();
        } else if (treeDensity >= forestDensityThreshold && surroundingForestNeighbourSpaces >= 8) {
            return mapScheme.getBaseColorForest();
        } else {
            return colorCalculator.modifyTransparency(mapScheme.getBaseColorForest(), (surroundingForestNeighbourSpaces + 1) * 0.125);
        }
    }

}
