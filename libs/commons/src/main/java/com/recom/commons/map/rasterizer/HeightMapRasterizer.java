package com.recom.commons.map.rasterizer;

import com.recom.commons.map.PixelBufferMapperUtil;
import com.recom.commons.map.rasterizer.configuration.LayerOrder;
import com.recom.commons.map.rasterizer.configuration.MapLayerRenderer;
import com.recom.commons.map.rasterizer.scaler.DEMScaler;
import com.recom.commons.model.DEMDescriptor;
import com.recom.commons.model.maprendererpipeline.MapComposerWorkPackage;
import com.recom.commons.model.maprendererpipeline.MapLayerRendererConfiguration;
import lombok.Getter;
import lombok.NonNull;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class HeightMapRasterizer implements MapLayerRenderer {

    @Getter
    @NonNull
    private MapLayerRendererConfiguration mapLayerRendererConfiguration = MapLayerRendererConfiguration.builder()
            .layerOrder(LayerOrder.HEIGHT_MAP)
            .build();
    @NonNull
    private final DEMScaler demScaler;


    public HeightMapRasterizer() {
        demScaler = new DEMScaler();
    }

    public int[] rasterizeScaledHeightMapRGB(
            @NonNull final DEMDescriptor DEMDescriptor,
            final int scale
    ) {
        final int[] originalHeightMap = rasterizeHeightMapRGB(DEMDescriptor);

        return demScaler.scaleMap(DEMDescriptor, scale, originalHeightMap);
    }

    @NonNull
    public ByteArrayOutputStream rasterizeHeightMapPNG(@NonNull final DEMDescriptor DEMDescriptor) throws IOException {
        final int[] pixelBuffer = rasterizeHeightMapRGB(DEMDescriptor);

        return PixelBufferMapperUtil.map(DEMDescriptor, pixelBuffer);
    }

    public int[] rasterizeHeightMapRGB(@NonNull final DEMDescriptor command) {
        final int width = command.getDemWidth();
        final int height = command.getDemHeight();
        final int[] imageBuffer = new int[width * height];

        final float heightRange = command.getMaxHeight() - command.getSeaLevel();
        final float depthRange = command.getMaxWaterDepth() - command.getSeaLevel();

        for (int x = 0; x < width; x++) {
            for (int z = 0; z < height; z++) {
                final float heightValue = command.getDem()[x][z];
                Color color;

                if (heightValue >= command.getSeaLevel()) {
                    // map height to color
                    final float dynamicHeightUnit = (heightValue - command.getSeaLevel()) / heightRange;
                    int grayValue = (int) (255 * dynamicHeightUnit); // normalize to 0..255
                    grayValue = Math.min(Math.max(grayValue, 0), 255); // ensure that the value is in the valid range
                    color = new Color(grayValue, grayValue, grayValue);
                } else {
                    // map depth to water color
                    final float dynamicDepthUnit = (heightValue - command.getSeaLevel()) / depthRange;
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
        final int[] rawHeightMap = rasterizeHeightMapRGB(workPackage.getMapComposerConfiguration().getDemDescriptor());
        workPackage.getPipelineArtifacts().addArtifact(this, rawHeightMap);
    }

}