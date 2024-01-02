package com.recom.rendertools.rasterizer;

import lombok.NonNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class HeightmapRasterizer {

    @NonNull
    public ByteArrayOutputStream rasterizeHeightMapPNG(@NonNull final HeightMapDescriptor command) throws IOException {
        final int width = command.getHeightMap().length;
        final int height = command.getHeightMap()[0].length;
        final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        final float heightRange = command.getMaxHeight() - command.getSeaLevel();
        final float depthRange = command.getMaxWaterDepth() - command.getSeaLevel();

        for (int x = 0; x < width; x++) {
            for (int z = 0; z < height; z++) {
                final float heightValue = command.getHeightMap()[x][z];
                Color color;

                if (heightValue >= command.getSeaLevel()) {
                    // map height to color
                    final float dynamicHeightValue = (heightValue - command.getSeaLevel()) / heightRange;
                    int grayValue = (int) (255 * dynamicHeightValue); // normalize to 0..255
                    grayValue = Math.min(Math.max(grayValue, 0), 255); // ensure that the value is in the valid range
                    color = new Color(grayValue, grayValue, grayValue);
                } else {
                    // map depth to water color
                    final float dynamicDepthValue = (heightValue - command.getSeaLevel()) / depthRange;
                    int blueValue = (int) (255 * (dynamicDepthValue)); //  // normalize to 0..255
                    blueValue = Math.min(Math.max(blueValue, 0), 255);
                    color = new Color((int) (blueValue * 0.77), (int) (192 * 0.94), blueValue);
                }

                image.setRGB(x, z, color.getRGB());
            }
        }

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", outputStream);

        return outputStream;
    }

}