package com.recom.commons.calculator.d8algorithm;


import com.recom.commons.calculator.ARGBCalculator;
import com.recom.commons.map.rasterizer.mapdesignscheme.MapDesignScheme;
import com.recom.commons.model.DEMDescriptor;
import com.recom.commons.model.maprendererpipeline.dataprovider.SpacialIndex;
import com.recom.commons.model.maprendererpipeline.dataprovider.village.VillageItem;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class D8AlgorithmForVillageMap {

    @NonNull
    private final ARGBCalculator colorCalculator = new ARGBCalculator();
    private final int villageCellSizeInMeter;


    @NonNull
    public int[][] generateVillageMap(
            @NonNull final DEMDescriptor demDescriptor,
            @NonNull final SpacialIndex<VillageItem> spacialIndex,
            @NonNull final MapDesignScheme mapScheme
    ) {
        final int demWidth = demDescriptor.getDemWidth();
        final int demHeight = demDescriptor.getDemHeight();

        final int[][] forestMap = new int[demWidth][demHeight];
        for (int demX = 0; demX < demWidth; demX++) {
            for (int demY = 0; demY < demHeight; demY++) {
                forestMap[demX][demY] = calculateVillageFragment(demDescriptor, spacialIndex, mapScheme, demX, demY);
            }
        }

        return forestMap;
    }

    @NonNull
    private int calculateVillageFragment(
            @NonNull final DEMDescriptor demDescriptor,
            @NonNull final SpacialIndex<VillageItem> spacialIndex,
            @NonNull final MapDesignScheme mapScheme,
            final int demX,
            final int demY
    ) {
        // 10 Trees per 100m²= // @TODO extract to conf
        final int forestCellSizeSquared = villageCellSizeInMeter * villageCellSizeInMeter;
        double villageDensityThreshold = 1F / 100; // @TODO extract to conf

        final int spacialX = (int) Math.ceil(demX * demDescriptor.getStepSize());
        final int spacialY = (int) Math.ceil(demY * demDescriptor.getStepSize());

        final List<VillageItem> forestItemsInSpace = spacialIndex.getInSpace(spacialX, spacialY);
        final double villageDensity = forestItemsInSpace.size() / (double) forestCellSizeSquared;

        int surroundingForestNeighbourSpaces = 0;
        for (int direction = 0; direction < 8; direction++) {
            final double adjacentNeighborSpatialX = spacialX + (D8AspectMatrix.directionXComponentMatrix[direction] * demDescriptor.getStepSize()); // Calculate the X-coordinate of the adjacent neighbor.
            final double adjacentNeighborSpatialY = spacialY + (D8AspectMatrix.directionYComponentMatrix[direction] * demDescriptor.getStepSize()); // Calculate the Y-coordinate of the adjacent neighbor.

            if (adjacentNeighborSpatialX < 0 || adjacentNeighborSpatialX > demDescriptor.getMapWidthInMeter() || adjacentNeighborSpatialY < 0 || adjacentNeighborSpatialY > demDescriptor.getMapHeightInMeter()) {
                continue;
            } else {
                final List<VillageItem> forestItemsInNeighborCell = spacialIndex.getInSpace(adjacentNeighborSpatialX, adjacentNeighborSpatialY);

                double neighbourVillageDensity;
                if (!forestItemsInNeighborCell.isEmpty()) {
                    neighbourVillageDensity = forestItemsInNeighborCell.size() / (double) forestCellSizeSquared;
                } else {
                    neighbourVillageDensity = 0;
                }

                if (neighbourVillageDensity >= villageDensityThreshold) {
                    surroundingForestNeighbourSpaces++;
                }
            }
        }

        final int baseColorVillage = mapScheme.getBaseColorVillage();

        if (villageDensity < villageDensityThreshold) {
            return mapScheme.getBaseColorContourBackground(); // @TODO extract to new variable baseColorOfForestBackground!!!
        } else if (villageDensity >= villageDensityThreshold && surroundingForestNeighbourSpaces >= 5) {
            return baseColorVillage;
        } else {
            return colorCalculator.modifyTransparency(baseColorVillage, 0.5);
        }
    }

}
