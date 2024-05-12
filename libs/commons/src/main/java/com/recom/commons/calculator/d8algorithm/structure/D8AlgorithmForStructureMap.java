package com.recom.commons.calculator.d8algorithm.structure;


import com.recom.commons.calculator.ARGBCalculator;
import com.recom.commons.calculator.d8algorithm.D8AspectMatrix;
import com.recom.commons.map.rasterizer.mapdesignscheme.MapDesignScheme;
import com.recom.commons.model.DEMDescriptor;
import com.recom.commons.model.maprendererpipeline.dataprovider.Cluster;
import com.recom.commons.model.maprendererpipeline.dataprovider.SpacialIndex;
import com.recom.commons.model.maprendererpipeline.dataprovider.structure.StructureItem;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class D8AlgorithmForStructureMap {

    @NonNull
    private final ARGBCalculator colorCalculator = new ARGBCalculator();


    @NonNull
    public int[][] generateStructureMap(
            @NonNull final DEMDescriptor demDescriptor,
            @NonNull final Cluster<StructureItem> structureCluster,
            @NonNull final MapDesignScheme mapScheme
    ) {
        final int demWidth = demDescriptor.getDemWidth();
        final int demHeight = demDescriptor.getDemHeight();

        final int[][] structureMap = new int[demWidth][demHeight];
        for (int demX = 0; demX < demWidth; demX++) {
            for (int demY = 0; demY < demHeight; demY++) {
                structureMap[demX][demY] = calculateStructureFragment(demDescriptor, structureCluster, mapScheme, demX, demY);
            }
        }

        return structureMap;
    }

    @NonNull
    private int calculateStructureFragment(
            @NonNull final DEMDescriptor demDescriptor,
            @NonNull final Cluster<StructureItem> structureCluster,
            @NonNull final MapDesignScheme mapScheme,
            final int demX,
            final int demY
    ) {
        final double spacialX = demX * demDescriptor.getStepSize().doubleValue();
        final double spacialY = demY * demDescriptor.getStepSize().doubleValue();

        final List<StructureItem> structureItemsInSpace = structureCluster.getItemsInSpace(spacialX, spacialY);
        final double StructureDensity = structureItemsInSpace.size() / structureCluster.getCellSizeInSquareMeter();

        int surroundingStructureNeighbourSpaces = 0;
        for (int direction = 0; direction < 8; direction++) {
            final double adjacentNeighborSpatialX = spacialX + (D8AspectMatrix.directionXComponentMatrix[direction] * structureCluster.getCellSizeInMeter()); // Calculate the X-coordinate of the adjacent neighbor.
            final double adjacentNeighborSpatialY = spacialY + (D8AspectMatrix.directionYComponentMatrix[direction] * structureCluster.getCellSizeInMeter()); // Calculate the Y-coordinate of the adjacent neighbor.

            if (adjacentNeighborSpatialX < 0 || adjacentNeighborSpatialX > demDescriptor.getMapWidthInMeter() || adjacentNeighborSpatialY < 0 || adjacentNeighborSpatialY > demDescriptor.getMapHeightInMeter()) {
                continue;
            } else {
                final List<StructureItem> structureItemsInNeighborCell = structureCluster.getItemsInSpace(adjacentNeighborSpatialX, adjacentNeighborSpatialY);

                final double neighbourStructureDensity;
                if (!structureItemsInNeighborCell.isEmpty()) {
                    neighbourStructureDensity = structureItemsInNeighborCell.size() / structureCluster.getCellSizeInSquareMeter();
                } else {
                    neighbourStructureDensity = 0;
                }

                if (neighbourStructureDensity >= structureCluster.getItemsPerSquareMeterThreshold()) {
                    surroundingStructureNeighbourSpaces++;
                }
            }
        }

        if (StructureDensity < structureCluster.getItemsPerSquareMeterThreshold()) {
            return mapScheme.getBaseColorStructureBackground();
        } else if (StructureDensity >= structureCluster.getItemsPerSquareMeterThreshold() && surroundingStructureNeighbourSpaces >= 8) {
            return mapScheme.getBaseColorStructure();
        } else {
            return colorCalculator.modifyTransparency(mapScheme.getBaseColorStructure(), surroundingStructureNeighbourSpaces * 0.125);
        }

//        if (StructureDensity < structureDensityThreshold) {
//            return mapScheme.getBaseColorStructureBackground();
//        } else if (StructureDensity >= structureDensityThreshold && surroundingStructureNeighbourSpaces >= 3) {
//            return mapScheme.getBaseColorStructure();
//        } else {
//            int transparencyFactor = 0x04;
//            return colorCalculator.setTransparency(mapScheme.getBaseColorStructure(), (surroundingStructureNeighbourSpaces + 1) * transparencyFactor);
//        }
    }

}
