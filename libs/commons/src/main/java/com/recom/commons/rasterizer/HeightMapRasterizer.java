package com.recom.commons.rasterizer;

import com.recom.commons.model.DEMDescriptor;
import com.recom.commons.model.MapRendererPipelineArtefacts;
import com.recom.commons.rasterizer.scaler.DEMScaler;
import com.recom.commons.rasterizer.meta.LayerOrder;
import com.recom.commons.rasterizer.meta.MapLayerPipelineRenderer;
import lombok.Getter;
import lombok.NonNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class HeightMapRasterizer implements MapLayerPipelineRenderer {

    @NonNull
    private final DEMScaler demScaler;
    @Getter
    private final LayerOrder layerOrder = LayerOrder.HEIGHT_MAP;
    @Getter
    private final boolean visible = true;


    public HeightMapRasterizer() {
        demScaler = new DEMScaler();
    }

    public int[] rasterizeScaledHeightMapRGB(
            @NonNull final DEMDescriptor DEMDescriptor,
            final int scale
    ) {
        final int[] originalHeightMap = rasterizeScaledHeightMapRGB(DEMDescriptor);

        return demScaler.scaleMap(DEMDescriptor, scale, originalHeightMap);
    }

    @NonNull
    public ByteArrayOutputStream rasterizeHeightMapPNG(@NonNull final DEMDescriptor DEMDescriptor) throws IOException {
        final int[] pixelBuffer = rasterizeScaledHeightMapRGB(DEMDescriptor);

        final int width = DEMDescriptor.getDemWidth();
        final int height = DEMDescriptor.getDemHeight();
        final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        final int[] imagePixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(pixelBuffer, 0, imagePixels, 0, pixelBuffer.length);

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", outputStream);

        return outputStream;
    }

    public int[] rasterizeScaledHeightMapRGB(@NonNull final DEMDescriptor command) {
        final int width = command.getDem().length;
        final int height = command.getDem()[0].length;
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

    @NonNull
    @Override
    public MapRendererPipelineArtefacts render(@NonNull final MapRendererPipelineArtefacts pipelineArtefacts) throws IOException {
        pipelineArtefacts.setRasterizedHeightMap(rasterizeHeightMapPNG(pipelineArtefacts.getDemDescriptor()));

        return pipelineArtefacts;
    }

}