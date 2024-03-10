package com.recom.commons.calculator.d8algorithm;


import com.recom.commons.calculator.ARGBCalculator;
import com.recom.commons.map.rasterizer.mapdesignscheme.MapDesignScheme;
import com.recom.commons.model.DEMDescriptor;
import com.recom.commons.model.maprendererpipeline.dataprovider.forest.ForestItem;
import com.recom.commons.model.maprendererpipeline.dataprovider.SpacialIndex;
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
        // 10 Trees per 100m²= // @TODO extract to conf
        final int forestCellSizeSquared = forestCellSizeInMeter * forestCellSizeInMeter;
        double forestDensityThreshold = 1F / 100; // @TODO extract to conf

        final int spacialX = demX * demDescriptor.getStepSize();
        final int spacialY = demY * demDescriptor.getStepSize();

        final List<ForestItem> forestItemsInSpace = spacialIndex.getInSpace(spacialX, spacialY);
        final double treeDensity = forestItemsInSpace.size() / (double) forestCellSizeSquared;

        int surroundingForestNeighbourSpaces = 0;
        for (int direction = 0; direction < 8; direction++) {
            final double adjacentNeighborSpatialX = spacialX + (D8AspectMatrix.directionXComponentMatrix[direction] * demDescriptor.getStepSize()); // Calculate the X-coordinate of the adjacent neighbor.
            final double adjacentNeighborSpatialY = spacialY + (D8AspectMatrix.directionYComponentMatrix[direction] * demDescriptor.getStepSize()); // Calculate the Y-coordinate of the adjacent neighbor.

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

        final int baseColorForest = mapScheme.getBaseColorForest();

        if (treeDensity < forestDensityThreshold) {
            return mapScheme.getBaseColorContourBackground(); // @TODO extract to new variable baseColorOfForestBackground!!!
        } else if (treeDensity >= forestDensityThreshold && surroundingForestNeighbourSpaces >= 5) {
            return baseColorForest;
        } else {
            return colorCalculator.modifyTransparency(baseColorForest, 0.5);
        }
    }

}
