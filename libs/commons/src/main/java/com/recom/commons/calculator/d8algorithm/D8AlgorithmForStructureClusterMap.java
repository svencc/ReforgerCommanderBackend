package com.recom.commons.calculator.d8algorithm;


import com.recom.commons.calculator.ARGBCalculator;
import com.recom.commons.map.rasterizer.mapdesignscheme.MapDesignScheme;
import com.recom.commons.model.DEMDescriptor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class D8AlgorithmForStructureClusterMap {

    @NonNull
    private final ARGBCalculator colorCalculator = new ARGBCalculator();


    @NonNull
    public int[][] generateStructureClusterMap(
            @NonNull final DEMDescriptor demDescriptor,
            @NonNull final MapDesignScheme mapScheme
    ) {
        final int demWidth = demDescriptor.getDemWidth();
        final int demHeight = demDescriptor.getDemHeight();

        final int[][] structureMap = new int[demWidth][demHeight];
        for (int demX = 0; demX < demWidth; demX++) {
            for (int demY = 0; demY < demHeight; demY++) {
                structureMap[demX][demY] = calculateStructureClusterFragment(demDescriptor, mapScheme, demX, demY);
            }
        }

        return structureMap;
    }

    @NonNull
    private int calculateStructureClusterFragment(
            @NonNull final DEMDescriptor demDescriptor,
            @NonNull final MapDesignScheme mapScheme,
            final int demX,
            final int demY
    ) {
        // @TODO <<<<<<<<<<<<<<<<<<<<<<<<< here <<<<<<<<<<<<<<<<<<<<<<<<<< <<<<<<<<<<<<<<<<<<<<<<<<<< <<<<<<<<<<<<<<<<<< <<<<<<<<<<<<<<<<<<
        return 0;
    }

}
