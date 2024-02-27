package com.recom.commons.map.rasterizer;

import com.recom.commons.calculator.d8algorithm.D8AlgorithmForShadedMap;
import com.recom.commons.map.rasterizer.configuration.LayerOrder;
import com.recom.commons.map.rasterizer.configuration.MapLayerRasterizer;
import com.recom.commons.map.rasterizer.mapdesignscheme.MapDesignScheme;
import com.recom.commons.model.DEMDescriptor;
import com.recom.commons.model.SlopeAndAspectMap;
import com.recom.commons.model.maprendererpipeline.CreatedArtifact;
import com.recom.commons.model.maprendererpipeline.MapComposerWorkPackage;
import com.recom.commons.model.maprendererpipeline.MapLayerRendererConfiguration;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;


@Getter
@Setter
public class ShadowedMapRasterizer implements MapLayerRasterizer {

    @NonNull
    private MapLayerRendererConfiguration mapLayerRendererConfiguration = MapLayerRendererConfiguration.builder()
            .rasterizerName(getClass().getSimpleName())
            .layerOrder(LayerOrder.SHADOWED_MAP)
            .build();


    @NonNull
    public int[] rasterizeShadowedMap(
            @NonNull final DEMDescriptor demDescriptor,
            @NonNull final SlopeAndAspectMap slopeAndAspectMap,
            @NonNull final MapDesignScheme mapScheme
    ) {
        final D8AlgorithmForShadedMap shadedMapAlgorithm = new D8AlgorithmForShadedMap();
        final int[][] shadedMap = shadedMapAlgorithm.generateShadedMap(slopeAndAspectMap.getSlopeAndAspectMap(), mapScheme);

        final int width = demDescriptor.getDemWidth();
        final int height = demDescriptor.getDemHeight();

        final int[] pixelBuffer = new int[width * height];
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < height; z++) {
                pixelBuffer[x + z * width] = shadedMap[x][z];
            }
        }

        return pixelBuffer;
    }

    @Override
    public String getRasterizerName() {
        return getClass().getSimpleName();
    }

    @Override
    public void render(@NonNull final MapComposerWorkPackage workPackage) {
        workPackage.getPipelineArtifacts().getArtifactFrom(SlopeAndAspectMapRasterizer.class).ifPresent((final CreatedArtifact artifact) -> {
            final Object artifactData = artifact.getData();
            if (artifactData instanceof SlopeAndAspectMap slopeAndAspectMap) {
                final DEMDescriptor demDescriptor = workPackage.getMapComposerConfiguration().getDemDescriptor();
                final MapDesignScheme mapDesignScheme = workPackage.getMapComposerConfiguration().getMapDesignScheme();

                final int[] rawShadowedMap = rasterizeShadowedMap(demDescriptor, slopeAndAspectMap, mapDesignScheme);
                workPackage.getPipelineArtifacts().addArtifact(this, rawShadowedMap);
            }
        });
    }

}