package com.recom.rendertools.rasterizer;

import lombok.NonNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class HeightmapRasterizer {

    @NonNull
    public ByteArrayOutputStream rasterizeHeightMapPNG(@NonNull final HeightMapDescriptor command) throws IOException {
        @NonNull int[] pixelBuffer = rasterizeHeightMapRGB(command);

        final int width = command.getHeightMap().length;
        final int height = command.getHeightMap()[0].length;
        final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

//        for (int x = 0; x < width; x++) {
//            for (int z = 0; z < height; z++) {
//                final int rgb = pixelBuffer[x + z * width]; // get the color from the int buffer array
//                image.setRGB(x, z, rgb);
//            }
//        }

        final int[] imagePixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(pixelBuffer, 0, imagePixels, 0, pixelBuffer.length);

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", outputStream);

        return outputStream;
    }

    public int[] rasterizeHeightMapRGB(@NonNull final HeightMapDescriptor command) {
        final int width = command.getHeightMap().length;
        final int height = command.getHeightMap()[0].length;
        final int[] imageBuffer = new int[width * height];

        final float heightRange = command.getMaxHeight() - command.getSeaLevel();
        final float depthRange = command.getMaxWaterDepth() - command.getSeaLevel();

        for (int x = 0; x < width; x++) {
            for (int z = 0; z < height; z++) {
                final float heightValue = command.getHeightMap()[x][z];
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

    public int[] rasterizeHeightMapARGB(@NonNull final HeightMapDescriptor command) {
        final int[] rgbPixels = rasterizeHeightMapRGB(command);

        // add solid alpha channel xff000000 value to each pixel
        for (int i = 0; i < rgbPixels.length; i++) {
            rgbPixels[i] = 0xff000000 | rgbPixels[i];
        }

        return rgbPixels;
    }

}