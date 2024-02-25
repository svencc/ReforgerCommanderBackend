package com.recom.commons.map.rasterizer;

import com.recom.commons.map.rasterizer.configuration.LayerOrder;
import com.recom.commons.map.rasterizer.configuration.MapLayerRenderer;
import com.recom.commons.map.rasterizer.scaler.DEMScaler;
import com.recom.commons.model.DEMDescriptor;
import com.recom.commons.model.maprendererpipeline.MapComposerWorkPackage;
import com.recom.commons.model.maprendererpipeline.MapLayerRendererConfiguration;
import lombok.Getter;
import lombok.NonNull;

import java.awt.*;


public class BaseMapRasterizer implements MapLayerRenderer {

    @Getter
    @NonNull
    private MapLayerRendererConfiguration mapLayerRendererConfiguration = MapLayerRendererConfiguration.builder()
            .layerOrder(LayerOrder.BASE_MAP)
            .build();
    @NonNull
    private final DEMScaler demScaler;


    public BaseMapRasterizer() {
        demScaler = new DEMScaler();
    }

    public int[] rasterizeScaledHeightMap(
            @NonNull final DEMDescriptor DEMDescriptor,
            final int scale
    ) {
        final int[] originalHeightMap = rasterizeHeightMap(DEMDescriptor);

        return demScaler.scaleMap(DEMDescriptor, scale, originalHeightMap);
    }

    public int[] rasterizeHeightMap(@NonNull final DEMDescriptor demDescriptor) {
        final int width = demDescriptor.getDemWidth();
        final int height = demDescriptor.getDemHeight();
        final int[] imageBuffer = new int[width * height];

        final float heightRange = demDescriptor.getMaxHeight() - demDescriptor.getSeaLevel();
        final float depthRange = demDescriptor.getMaxWaterDepth() - demDescriptor.getSeaLevel();

        for (int x = 0; x < width; x++) {
            for (int z = 0; z < height; z++) {
                final float heightValue = demDescriptor.getDem()[x][z];
                Color color;

                if (heightValue >= demDescriptor.getSeaLevel()) {
                    // map height to color
                    final float dynamicHeightUnit = (heightValue - demDescriptor.getSeaLevel()) / heightRange;
                    int grayValue = (int) (255 * dynamicHeightUnit); // normalize to 0..255
                    grayValue = Math.min(Math.max(grayValue, 0), 255); // ensure that the value is in the valid range
                    color = new Color(grayValue, grayValue, grayValue);
                } else {
                    // map depth to water color
                    final float dynamicDepthUnit = (heightValue - demDescriptor.getSeaLevel()) / depthRange;
                    int blueValue = (int) (255 * (dynamicDepthUnit)); //  // normalize to 0..255
                    blueValue = Math.min(Math.max(blueValue, 0), 255); // ensure that the value is in the valid range
                    color = new Color((int) (blueValue * 0.77), (int) (192 * 0.94), blueValue);
                }

                imageBuffer[x + z * width] = color.getRGB();
            }
        }

        return imageBuffer;
    }

    @Override
    public void render(@NonNull final MapComposerWorkPackage workPackage) {
        final int[] rawHeightMap = rasterizeHeightMap(workPackage.getMapComposerConfiguration().getDemDescriptor());
        workPackage.getPipelineArtifacts().addArtifact(this, rawHeightMap);
    }

}