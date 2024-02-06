package com.recom.rendertools.rasterizer;

import com.recom.rendertools.calculator.ARGBCalculator;
import lombok.NonNull;

public class HeightmapScaler {


    public int[] scaleMap(
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
        } else {
            // Nearest Neighbour Downsampling: https://en.wikipedia.org/wiki/Image_scaling
            final int absScale = Math.abs(scale);
            final int scaledHeight = heightMapDescriptor.getHeightMap().length / absScale;
            final int scaledWidth = heightMapDescriptor.getHeightMap()[0].length / absScale;

            final int[] scaledMap = new int[scaledHeight * scaledWidth];
            for (int x = 0; x < scaledHeight; x++) {
                for (int z = 0; z < scaledWidth; z++) {
                    int alphaComponentSum = 0;
                    int redComponentSum = 0;
                    int greenComponentSum = 0;
                    int blueComponentSum = 0;
                    for (int scaledPixelX = 0; scaledPixelX < absScale; scaledPixelX++) {
                        for (int scaledPixelZ = 0; scaledPixelZ < absScale; scaledPixelZ++) {
                            alphaComponentSum += ARGBCalculator.getAlphaComponent(originalHeightMap[(x * absScale + scaledPixelX) + (z * absScale + scaledPixelZ) * originalWidth]);
                            redComponentSum += ARGBCalculator.getRedComponent(originalHeightMap[(x * absScale + scaledPixelX) + (z * absScale + scaledPixelZ) * originalWidth]);
                            greenComponentSum += ARGBCalculator.getGreenComponent(originalHeightMap[(x * absScale + scaledPixelX) + (z * absScale + scaledPixelZ) * originalWidth]);
                            blueComponentSum += ARGBCalculator.getBlueComponent(originalHeightMap[(x * absScale + scaledPixelX) + (z * absScale + scaledPixelZ) * originalWidth]);
                        }
                    }
                    final int scalePow = (int) Math.pow(absScale, 2);
                    alphaComponentSum /= scalePow;
                    redComponentSum /= scalePow;
                    greenComponentSum /= scalePow;
                    blueComponentSum /= scalePow;

                    scaledMap[x + z * scaledWidth] = ARGBCalculator.compose(alphaComponentSum, redComponentSum, greenComponentSum, blueComponentSum);
                }
            }

            return scaledMap;
        }
    }

}