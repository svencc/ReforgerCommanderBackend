package com.recom.commons.map.rasterizer;

import com.recom.commons.calculator.ARGBCalculator;
import com.recom.commons.calculator.ARGBColor;
import com.recom.commons.map.rasterizer.configuration.LayerOrder;
import com.recom.commons.map.rasterizer.configuration.MapLayerRasterizer;
import com.recom.commons.map.rasterizer.interpolation.DEMInterpolationAlgorithm;
import com.recom.commons.map.rasterizer.mapdesignscheme.MapDesignScheme;
import com.recom.commons.map.rasterizer.interpolation.DEMInterpolationAlgorithmNearestNeighbor;
import com.recom.commons.model.DEMDescriptor;
import com.recom.commons.model.maprendererpipeline.MapComposerWorkPackage;
import com.recom.commons.model.maprendererpipeline.MapLayerRasterizerConfiguration;
import lombok.Getter;
import lombok.NonNull;


public class BaseMapRasterizer implements MapLayerRasterizer {

    @NonNull
    private final ARGBCalculator argbCalculator = new ARGBCalculator();
    @Getter
    @NonNull
    private MapLayerRasterizerConfiguration mapLayerRasterizerConfiguration = MapLayerRasterizerConfiguration.builder()
            .rasterizerName(getClass().getSimpleName())
            .layerOrder(LayerOrder.BASE_MAP)
            .build();
    @NonNull
    private final DEMInterpolationAlgorithm demInterpolatorAlgorithm;


    public BaseMapRasterizer() {
        // @TODO vermutlich hier raus; has to be used outside ...
        demInterpolatorAlgorithm = new DEMInterpolationAlgorithmNearestNeighbor();
    }

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

        for (int x = 0; x < width; x++) {
            for (int z = 0; z < height; z++) {
                final float heightValue = demDescriptor.getDem()[x][z];
                int color;

                if (heightValue >= demDescriptor.getSeaLevel()) {
                    // map height to color
                    final float dynamicHeightUnit = (heightValue - demDescriptor.getSeaLevel()) / heightRange;
                    int grayValue = (int) (255 * dynamicHeightUnit); // normalize to 0..255
                    grayValue = Math.min(Math.max(grayValue, 0), 255); // ensure that the value is in the valid range
                    color = ARGBColor.ARGB(255, grayValue, grayValue, grayValue);
                } else {
                    // map depth to water color
                    final float dynamicDepthUnit = (heightValue - demDescriptor.getSeaLevel()) / depthRange;
                    color = argbCalculator.modifyBrightness(mapScheme.getBaseColorWater(), Math.abs(1 - dynamicDepthUnit));
                }

                imageBuffer[x + z * width] = color;
            }
        }

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

}