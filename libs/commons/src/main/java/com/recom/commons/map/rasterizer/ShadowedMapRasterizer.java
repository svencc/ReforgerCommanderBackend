package com.recom.commons.map.rasterizer;

import com.recom.commons.calculator.d8algorithm.D8AlgorithmForShadedMap;
import com.recom.commons.map.rasterizer.configuration.LayerOrder;
import com.recom.commons.map.rasterizer.configuration.MapLayerRasterizer;
import com.recom.commons.map.rasterizer.mapdesignscheme.MapDesignScheme;
import com.recom.commons.model.DEMDescriptor;
import com.recom.commons.model.SlopeAndAspectMap;
import com.recom.commons.model.maprendererpipeline.CreatedArtifact;
import com.recom.commons.model.maprendererpipeline.MapComposerWorkPackage;
import com.recom.commons.model.maprendererpipeline.MapLayerRasterizerConfiguration;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.stream.IntStream;


@Getter
@Setter
public class ShadowedMapRasterizer implements MapLayerRasterizer {

    @NonNull
    private MapLayerRasterizerConfiguration mapLayerRasterizerConfiguration = MapLayerRasterizerConfiguration.builder()
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
        IntStream.range(0, width).parallel().forEach(demX -> {
            for (int demY = 0; demY < height; demY++) {
                pixelBuffer[demX + demY * width] = shadedMap[demX][demY];
            }
        });

        return pixelBuffer;
    }

    @Override
    public String getRasterizerName() {
        return getClass().getSimpleName();
    }

    @Override
    public void prepareAsync(@NonNull final MapComposerWorkPackage workPackage) {
        return;
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