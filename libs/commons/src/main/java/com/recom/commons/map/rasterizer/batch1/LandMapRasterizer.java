package com.recom.commons.map.rasterizer.batch1;

import com.recom.commons.calculator.ARGBCalculator;
import com.recom.commons.calculator.ARGBColor;
import com.recom.commons.map.rasterizer.configuration.BatchOrder;
import com.recom.commons.map.rasterizer.configuration.LayerOrder;
import com.recom.commons.map.rasterizer.configuration.MapLayerRasterizer;
import com.recom.commons.map.rasterizer.mapdesignscheme.MapDesignScheme;
import com.recom.commons.model.DEMDescriptor;
import com.recom.commons.model.maprendererpipeline.CreatedArtifact;
import com.recom.commons.model.maprendererpipeline.MapComposerWorkPackage;
import com.recom.commons.model.maprendererpipeline.MapLayerRasterizerConfiguration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Optional;
import java.util.stream.IntStream;


@NoArgsConstructor
public class LandMapRasterizer implements MapLayerRasterizer<int[]> {

    @NonNull
    private final ARGBCalculator argbCalculator = new ARGBCalculator();

    @Getter
    @NonNull
    private final MapLayerRasterizerConfiguration mapLayerRasterizerConfiguration = MapLayerRasterizerConfiguration.builder()
            .rasterizerName(getClass().getSimpleName())
            .batch(BatchOrder.BATCH_1)
            .layerOrder(LayerOrder.LAND_MAP)
            .enabled(false)
            .build();

    @NonNull
    private int[] rasterizeBaseMap(
            @NonNull final DEMDescriptor demDescriptor,
            @NonNull final MapDesignScheme mapScheme
    ) {
        final int width = demDescriptor.getDemWidth();
        final int height = demDescriptor.getDemHeight();
        final int[] imageBuffer = new int[height * width];

        final float heightRange = demDescriptor.getMaxHeight() - demDescriptor.getSeaLevel();
        final float depthRange = demDescriptor.getMaxWaterDepth() - demDescriptor.getSeaLevel();

        IntStream.range(0, height).parallel().forEach(demY -> {
            for (int demX = 0; demX < width; demX++) {
                final float heightValue = demDescriptor.getDem()[demY][demX];
                int color;

                if (heightValue > demDescriptor.getSeaLevel()) {
                    // map height to color
                    final float dynamicHeightUnit = (heightValue - demDescriptor.getSeaLevel()) / heightRange;
                    int maxHeightKeyValue = 255; // @TODO <<<< candidate for extraction into scheme
                    int greyValue = (int) (maxHeightKeyValue * dynamicHeightUnit); // normalize to 0..255
                    greyValue = Math.min(Math.max(greyValue, 0), 255); // ensure that the value is in the valid range

                    int valueHowTransparentTheHeightmapIs = 16; // @TODO <<<< candidate for extraction into scheme
                    color = ARGBColor.ARGB(valueHowTransparentTheHeightmapIs, greyValue, greyValue, greyValue);
                    color = argbCalculator.blend(color, mapScheme.getBaseColorTerrain());
                } else {
                    color = 0xFF000000;
                }

                imageBuffer[(demY * width) + demX] = color;
            }
        });

        return imageBuffer;
    }

    @Override
    public String getRasterizerName() {
        return getClass().getSimpleName();
    }

    @Override
    public void render(@NonNull final MapComposerWorkPackage workPackage) {
        final int[] rawBaseMap = rasterizeBaseMap(workPackage.getMapComposerConfiguration().getDemDescriptor(), workPackage.getMapComposerConfiguration().getMapDesignScheme());
        workPackage.getPipelineArtifacts().addArtifact(this, rawBaseMap);
    }

    @NonNull
    public Optional<int[]> findMyArtefactFromWorkPackage(@NonNull final MapComposerWorkPackage workPackage) {
        return workPackage.getPipelineArtifacts().getArtifactFrom(getClass()).map(CreatedArtifact::getData);
    }

}