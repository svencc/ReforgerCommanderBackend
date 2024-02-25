package com.recom.commons.map.rasterizer;

import com.recom.commons.calculator.d8algorithm.D8AlgorithmForShadedMap;
import com.recom.commons.calculator.d8algorithm.D8AlgorithmForSlopeAndAspectMap;
import com.recom.commons.map.PixelBufferMapper;
import com.recom.commons.map.rasterizer.configuration.LayerOrder;
import com.recom.commons.map.rasterizer.configuration.MapLayerRenderer;
import com.recom.commons.map.rasterizer.mapdesignscheme.MapDesignScheme;
import com.recom.commons.model.DEMDescriptor;
import com.recom.commons.model.SlopeAndAspect;
import com.recom.commons.model.maprendererpipeline.MapComposerWorkPackage;
import com.recom.commons.model.maprendererpipeline.MapLayerRendererConfiguration;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


@Getter
@Setter
public class ShadowedMapRasterizer implements MapLayerRenderer {

    @NonNull
    private MapLayerRendererConfiguration mapLayerRendererConfiguration = MapLayerRendererConfiguration.builder()
            .layerOrder(LayerOrder.SHADOWED_MAP)
            .build();


    @NonNull
    public ByteArrayOutputStream rasterizeShadowedMapPNG(
            @NonNull final DEMDescriptor DEMDescriptor,
            @NonNull final MapDesignScheme mapScheme
    ) throws IOException {
        final int[] pixelBuffer = rasterizeShadowedMapRaw(DEMDescriptor, mapScheme);

        return PixelBufferMapper.map(DEMDescriptor, pixelBuffer);
    }

    @NonNull
    public int[] rasterizeShadowedMapRaw(
            @NonNull final DEMDescriptor DEMDescriptor,
            @NonNull final MapDesignScheme mapScheme
    ) {
        final float[][] dem = DEMDescriptor.getDem(); // @TODO rename heightMap to dem
        final D8AlgorithmForSlopeAndAspectMap slopeAndAspectAlgorithm = new D8AlgorithmForSlopeAndAspectMap(5.0);
        final D8AlgorithmForShadedMap shadedMapAlgorithm = new D8AlgorithmForShadedMap();

        final SlopeAndAspect[][] slopeAndAspects = slopeAndAspectAlgorithm.generateSlopeAndAspectMap(dem);
        final int[][] shadedMap = shadedMapAlgorithm.generateShadedMap(slopeAndAspects, mapScheme);

        final int width = DEMDescriptor.getDemWidth();
        final int height = DEMDescriptor.getDemHeight();

        final int[] pixelBuffer = new int[width * height];
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < height; z++) {
                pixelBuffer[x + z * width] = shadedMap[x][z];
            }
        }

        return pixelBuffer;
    }

    @Override
    public void render(@NonNull final MapComposerWorkPackage workPackage) {
        final int[] rawShadowedMap = rasterizeShadowedMapRaw(workPackage.getMapComposerConfiguration().getDemDescriptor(), workPackage.getMapComposerConfiguration().getMapDesignScheme());
        workPackage.getPipelineArtifacts().addArtifact(this, rawShadowedMap);
    }

}