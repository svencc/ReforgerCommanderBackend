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

    public int[] rasterizeHeightMapRGB(
            @NonNull final HeightMapDescriptor heightMapDescriptor,
            final int scale
    ) {
        final int[] originalHeightMap = rasterizeHeightMapRGB(heightMapDescriptor);

        return scaleMap(heightMapDescriptor, scale, originalHeightMap);
    }

    private static int[] scaleMap(
            @NonNull final HeightMapDescriptor heightMapDescriptor,
            final int scale,
            final int[] originalHeightMap
    ) {
        final int originalHeight = heightMapDescriptor.getHeightMap().length;
        final int originalWidth = heightMapDescriptor.getHeightMap()[0].length;

        if (Math.abs(scale) == 1 || scale == 0) {
            return originalHeightMap;
        } else if (scale > 1) {
            final int scaledHeight = heightMapDescriptor.getHeightMap().length * scale;
            final int scaledWidth = heightMapDescriptor.getHeightMap()[0].length * scale;

            final int[] scaledMap = new int[scaledHeight * scaledWidth];
            for (int x = 0; x < originalHeight; x++) {
                for (int z = 0; z < originalWidth; z++) {
                    final int color = originalHeightMap[x + z * originalWidth];
                    for (int scaledPixelX = 0; scaledPixelX < scale; scaledPixelX++) {
                        for (int scaledPixelZ = 0; scaledPixelZ < scale; scaledPixelZ++) {
                            scaledMap[(x * scale + scaledPixelX) + (z * scale + scaledPixelZ) * scaledWidth] = color;
                        }
                    }
                }
            }

            return scaledMap;
//        } else {
//            final int absScale = Math.abs(scale);
//            final int scaledHeight = command.getHeightMap().length / absScale;
//            final int scaledWidth = command.getHeightMap()[0].length / absScale;
//
//            final int[] scaledMap = new int[scaledHeight * scaledWidth];
//            for (int x = 0; x < scaledHeight; x++) {
//                for (int z = 0; z < scaledWidth; z++) {
//                    int color = 0;
//                    // wir müssen die Farben der umliegenden Pixel addieren und den durchschnitt berechnen + wir müssen den alpha kanal bei der addition und druchschittsberechnung rausnehmen und danach wieder anwenden
//                    for (int scaledPixelX = 0; scaledPixelX < absScale; scaledPixelX++) {
//                        for (int scaledPixelZ = 0; scaledPixelZ < absScale; scaledPixelZ++) {
//                            color += originalHeightMap[(x * absScale + scaledPixelX) + (z * absScale + scaledPixelZ) * originalWidth];
//                        }
//                    }
//                    color /= absScale * absScale;
//                    scaledMap[x + z * scaledWidth] = color;
//                }
//            }
//
//            return scaledMap;
//        }
        } else {
            final int absScale = Math.abs(scale);
            final int scaledHeight = heightMapDescriptor.getHeightMap().length / absScale;
            final int scaledWidth = heightMapDescriptor.getHeightMap()[0].length / absScale;

            final int[] scaledMap = new int[scaledHeight * scaledWidth];
            for (int x = 0; x < scaledHeight; x++) {
                for (int z = 0; z < scaledWidth; z++) {
                    int originalX = x * absScale;
                    int originalZ = z * absScale;
                    int originalIndex = originalX + originalZ * originalWidth;

                    int color = originalHeightMap[originalIndex];
                    scaledMap[x + z * scaledWidth] = color;
                }
            }

            return scaledMap;
        }

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