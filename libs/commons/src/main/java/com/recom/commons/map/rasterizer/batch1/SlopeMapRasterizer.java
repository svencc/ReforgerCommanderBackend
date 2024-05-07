package com.recom.commons.map.rasterizer.batch1;

import com.recom.commons.calculator.d8algorithm.D8AlgorithmForSlopeAndAspectMap;
import com.recom.commons.calculator.d8algorithm.D8AlgorithmForSlopeMap;
import com.recom.commons.map.rasterizer.configuration.BatchOrder;
import com.recom.commons.map.rasterizer.configuration.LayerOrder;
import com.recom.commons.map.rasterizer.configuration.MapLayerRasterizer;
import com.recom.commons.map.rasterizer.mapdesignscheme.MapDesignScheme;
import com.recom.commons.model.DEMDescriptor;
import com.recom.commons.model.SlopeAndAspect;
import com.recom.commons.model.maprendererpipeline.MapComposerWorkPackage;
import com.recom.commons.model.maprendererpipeline.MapLayerRasterizerConfiguration;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.stream.IntStream;


@Getter
@Setter
public class SlopeMapRasterizer implements MapLayerRasterizer {

    @NonNull
    private final MapLayerRasterizerConfiguration mapLayerRasterizerConfiguration = MapLayerRasterizerConfiguration.builder()
            .rasterizerName(getClass().getSimpleName())
            .batch(BatchOrder.SLOPE_AND_ASPECT_BATCH)
            .layerOrder(LayerOrder.SLOPE_MAP)
            .enabled(false)
            .build();


    @NonNull
    private int[] rasterizeSlopeMap(
            @NonNull final DEMDescriptor DEMDescriptor,
            @NonNull final MapDesignScheme mapScheme
    ) {
        final D8AlgorithmForSlopeAndAspectMap algorithmForSlopeAndAspect = new D8AlgorithmForSlopeAndAspectMap(DEMDescriptor.getStepSize().doubleValue());
        final D8AlgorithmForSlopeMap d8AlgorithmForSlopeMap = new D8AlgorithmForSlopeMap();

        final SlopeAndAspect[][] slopeAndAspects = algorithmForSlopeAndAspect.generateSlopeAndAspectMap(DEMDescriptor.getDem());
        final int[][] contourMap = d8AlgorithmForSlopeMap.generateSlopeMap(slopeAndAspects, mapScheme);

        final int width = DEMDescriptor.getDemWidth();
        final int height = DEMDescriptor.getDemHeight();

        final int[] pixelBuffer = new int[width * height];
        IntStream.range(0, width).parallel().forEach(demX -> {
            for (int demY = 0; demY < height; demY++) {
                pixelBuffer[demX + demY * width] = contourMap[demX][demY];
            }
        });

        return pixelBuffer;
    }

    @Override
    public String getRasterizerName() {
        return getClass().getSimpleName();
    }

    @Override
    public void render(@NonNull final MapComposerWorkPackage workPackage) {
        final int[] rawSlopeMap = rasterizeSlopeMap(workPackage.getMapComposerConfiguration().getDemDescriptor(), workPackage.getMapComposerConfiguration().getMapDesignScheme());
        workPackage.getPipelineArtifacts().addArtifact(this, rawSlopeMap);
    }

}