package com.recom.commons.calculator.d8algorithm;


import com.recom.commons.calculator.ARGBCalculator;
import com.recom.commons.map.rasterizer.mapdesignscheme.MapDesignScheme;
import com.recom.commons.model.DEMDescriptor;
import com.recom.commons.model.maprendererpipeline.dataprovider.SpacialIndex;
import com.recom.commons.model.maprendererpipeline.dataprovider.structure.StructureItem;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class D8AlgorithmForStructureMap {

    @NonNull
    private final ARGBCalculator colorCalculator = new ARGBCalculator();
    private final int structureCellSizeInMeter;


    @NonNull
    public int[][] generateStructureMap(
            @NonNull final DEMDescriptor demDescriptor,
            @NonNull final SpacialIndex<StructureItem> spacialIndex,
            @NonNull final MapDesignScheme mapScheme
    ) {
        final int demWidth = demDescriptor.getDemWidth();
        final int demHeight = demDescriptor.getDemHeight();

        final int[][] structureMap = new int[demWidth][demHeight];
        for (int demX = 0; demX < demWidth; demX++) {
            for (int demY = 0; demY < demHeight; demY++) {
                structureMap[demX][demY] = calculateStructureFragment(demDescriptor, spacialIndex, mapScheme, demX, demY);
            }
        }

        return structureMap;
    }

    @NonNull
    private int calculateStructureFragment(
            @NonNull final DEMDescriptor demDescriptor,
            @NonNull final SpacialIndex<StructureItem> spacialIndex,
            @NonNull final MapDesignScheme mapScheme,
            final int demX,
            final int demY
    ) {
        final int structureCellSizeSquared = structureCellSizeInMeter * structureCellSizeInMeter;
        double structureDensityThreshold = 1F / 100; // @TODO extract to conf

        final int spacialX = demX * demDescriptor.getStepSize();
        final int spacialY = demY * demDescriptor.getStepSize();

        final List<StructureItem> structureItemsInSpace = spacialIndex.getInSpace(spacialX, spacialY);
        final double StructureDensity = structureItemsInSpace.size() / (double) structureCellSizeSquared;

        int surroundingStructureNeighbourSpaces = 0;
        for (int direction = 0; direction < 8; direction++) {
            final double adjacentNeighborSpatialX = spacialX + (D8AspectMatrix.directionXComponentMatrix[direction] * spacialIndex.getCellSizeInMeter()); // Calculate the X-coordinate of the adjacent neighbor.
            final double adjacentNeighborSpatialY = spacialY + (D8AspectMatrix.directionYComponentMatrix[direction] * spacialIndex.getCellSizeInMeter()); // Calculate the Y-coordinate of the adjacent neighbor.

            if (adjacentNeighborSpatialX < 0 || adjacentNeighborSpatialX > demDescriptor.getMapWidthInMeter() || adjacentNeighborSpatialY < 0 || adjacentNeighborSpatialY > demDescriptor.getMapHeightInMeter()) {
                continue;
            } else {
                final List<StructureItem> structureItemsInNeighborCell = spacialIndex.getInSpace(adjacentNeighborSpatialX, adjacentNeighborSpatialY);

                double neighbourStructureDensity;
                if (!structureItemsInNeighborCell.isEmpty()) {
                    neighbourStructureDensity = structureItemsInNeighborCell.size() / (double) structureCellSizeSquared;
                } else {
                    neighbourStructureDensity = 0;
                }

                if (neighbourStructureDensity >= structureDensityThreshold) {
                    surroundingStructureNeighbourSpaces++;
                }
            }
        }

        if (StructureDensity < structureDensityThreshold) {
            return mapScheme.getBaseColorStructureBackground();
        } else if (StructureDensity >= structureDensityThreshold && surroundingStructureNeighbourSpaces >= 5) {
            return mapScheme.getBaseColorStructure();
        } else {
            return colorCalculator.modifyTransparency(mapScheme.getBaseColorStructure(), surroundingStructureNeighbourSpaces * 0.1);
        }
    }

}
