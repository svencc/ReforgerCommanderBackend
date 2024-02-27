package com.recom.commons.map.rasterizer;

import com.recom.commons.calculator.d8algorithm.D8AlgorithmForContourMap;
import com.recom.commons.map.rasterizer.configuration.LayerOrder;
import com.recom.commons.map.rasterizer.configuration.MapLayerRenderer;
import com.recom.commons.map.rasterizer.mapdesignscheme.MapDesignScheme;
import com.recom.commons.model.DEMDescriptor;
import com.recom.commons.model.maprendererpipeline.MapComposerWorkPackage;
import com.recom.commons.model.maprendererpipeline.MapLayerRendererConfiguration;
import lombok.Getter;
import lombok.NonNull;


@Getter
public class ContourMapRasterizer implements MapLayerRenderer {

    @NonNull
    private MapLayerRendererConfiguration mapLayerRendererConfiguration = MapLayerRendererConfiguration.builder()
            .layerOrder(LayerOrder.CONTOUR_MAP)
            .build();


    @NonNull
    public int[] rasterizeContourMap(
            @NonNull final DEMDescriptor DEMDescriptor,
            @NonNull final MapDesignScheme mapScheme
    ) {
        final D8AlgorithmForContourMap algorithmForContourMap = new D8AlgorithmForContourMap();

        final int[][] contourMap = algorithmForContourMap.generateContourMap(DEMDescriptor, mapScheme);

        final int width = DEMDescriptor.getDemWidth();
        final int height = DEMDescriptor.getDemHeight();

        final int[] pixelBuffer = new int[width * height];
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < height; z++) {
                pixelBuffer[x + z * width] = contourMap[x][z];
            }
        }

        return pixelBuffer;
    }

    @Override
    public void render(@NonNull final MapComposerWorkPackage workPackage) {
        final int[] rawContourMap = rasterizeContourMap(workPackage.getMapComposerConfiguration().getDemDescriptor(), workPackage.getMapComposerConfiguration().getMapDesignScheme());
        workPackage.getPipelineArtifacts().addArtifact(this, rawContourMap);
    }

}