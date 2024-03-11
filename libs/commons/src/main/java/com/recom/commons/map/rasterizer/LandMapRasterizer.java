package com.recom.commons.map.rasterizer;

import com.recom.commons.calculator.ARGBCalculator;
import com.recom.commons.calculator.ARGBColor;
import com.recom.commons.map.rasterizer.configuration.LayerOrder;
import com.recom.commons.map.rasterizer.configuration.MapLayerRasterizer;
import com.recom.commons.map.rasterizer.mapdesignscheme.MapDesignScheme;
import com.recom.commons.model.DEMDescriptor;
import com.recom.commons.model.maprendererpipeline.MapComposerWorkPackage;
import com.recom.commons.model.maprendererpipeline.MapLayerRasterizerConfiguration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.stream.IntStream;


@NoArgsConstructor
public class LandMapRasterizer implements MapLayerRasterizer {

    @NonNull
    private final ARGBCalculator argbCalculator = new ARGBCalculator();
    @Getter
    @NonNull
    private MapLayerRasterizerConfiguration mapLayerRasterizerConfiguration = MapLayerRasterizerConfiguration.builder()
            .rasterizerName(getClass().getSimpleName())
            .layerOrder(LayerOrder.LAND_MAP)
            .build();


    @NonNull
    public int[] rasterizeBaseMap(
            @NonNull final DEMDescriptor demDescriptor,
            @NonNull final MapDesignScheme mapScheme
    ) {
        final int width = demDescriptor.getDemWidth();
        final int height = demDescriptor.getDemHeight();
        final int[] imageBuffer = new int[width * height];

        final float heightRange = demDescriptor.getMaxHeight() - demDescriptor.getSeaLevel();
        final float depthRange = demDescriptor.getMaxWaterDepth() - demDescriptor.getSeaLevel();

        IntStream.range(0, width).parallel().forEach(demX -> {
            for (int demY = 0; demY < height; demY++) {
                final float heightValue = demDescriptor.getDem()[demX][demY];
                int color;

                if (heightValue > demDescriptor.getSeaLevel()) {
                    // map height to color
                    final float dynamicHeightUnit = (heightValue - demDescriptor.getSeaLevel()) / heightRange;
                    int maxHeightKeyValue = 255; // @TODO <<<< candidate for extraction into scheme
                    int grayValue = (int) (maxHeightKeyValue * dynamicHeightUnit); // normalize to 0..255
                    grayValue = Math.min(Math.max(grayValue, 0), 255); // ensure that the value is in the valid range

                    int valueHowTransparentTheHeightmapIs = 16; // @TODO <<<< candidate for extraction into scheme
                    color = ARGBColor.ARGB(valueHowTransparentTheHeightmapIs, grayValue, grayValue, grayValue);
                    color = argbCalculator.blend(color, mapScheme.getBaseColorTerrain());
                } else {
                    color = 0xFF000000;
                }

                imageBuffer[demX + demY * width] = color;
            }
        });

        return imageBuffer;
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
        final int[] rawBaseMap = rasterizeBaseMap(workPackage.getMapComposerConfiguration().getDemDescriptor(), workPackage.getMapComposerConfiguration().getMapDesignScheme());
        workPackage.getPipelineArtifacts().addArtifact(this, rawBaseMap);
    }

}