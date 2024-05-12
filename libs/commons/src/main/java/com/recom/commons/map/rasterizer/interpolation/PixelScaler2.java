package com.recom.commons.map.rasterizer.interpolation;

import com.recom.commons.calculator.ARGBCalculator;
import com.recom.commons.model.DEMDescriptor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PixelScaler2 {

    @NonNull
    private final ARGBCalculator argbCalculator = new ARGBCalculator();


    @NonNull
    public float[][] scale(
            @NonNull final DEMDescriptor demDescriptor,
            final int scale
    ) {
        final float[][] rasterizedHeightmap = demDescriptor.getDem();


        final int originalHeight = demDescriptor.getDem().length;
        final int originalWidth = demDescriptor.getDem()[0].length;

        if (Math.abs(scale) == 1 || scale == 0) {
            return rasterizedHeightmap;
        } else if (scale < 1) {
            final int scaledHeight = demDescriptor.getDem().length * scale;
            final int scaledWidth = demDescriptor.getDem()[0].length * scale;

            final float[][] scaledMap = new float[scaledHeight][scaledWidth];
            for (int coordinateY = 0; coordinateY < originalHeight; coordinateY++) {
                for (int coordinateX = 0; coordinateX < originalWidth; coordinateX++) {
                    final float color = rasterizedHeightmap[coordinateY][coordinateX];

                    for (int scaledPixelY = 0; scaledPixelY < scale; scaledPixelY++) {
                        for (int scaledPixelX = 0; scaledPixelX < scale; scaledPixelX++) {
                            // scaledMap[(coordinateY * scale + scaledPixelY) + (coordinateX * scale + scaledPixelX) * scaledWidth] = color;
                            scaledMap[scaledPixelY][scaledPixelX] = color;
                        }
                    }
                }
            }

            return scaledMap;
        } else {
            // Nearest Neighbour Downsampling: https://en.wikipedia.org/wiki/Image_scaling
            final int absScale = Math.abs(scale);
            final int scaledHeight = demDescriptor.getDem().length / absScale;
            final int scaledWidth = demDescriptor.getDem()[0].length / absScale;

//            final int[] scaledMap = new int[scaledHeight * scaledWidth];
            final float[][] scaledMap = new float[scaledHeight][scaledWidth];
            for (int coordinateY = 0; coordinateY < scaledHeight; coordinateY++) {
                for (int coordinateX = 0; coordinateX < scaledWidth; coordinateX++) {
                    int alphaComponentSum = 0;
                    int redComponentSum = 0;
                    int greenComponentSum = 0;
                    int blueComponentSum = 0;
                    for (int scaledPixelY = 0; scaledPixelY < absScale; scaledPixelY++) {
                        for (int scaledPixelX = 0; scaledPixelX < absScale; scaledPixelX++) {
                            alphaComponentSum += argbCalculator.getAlphaComponent((int) rasterizedHeightmap[coordinateY + scaledPixelY][coordinateX + scaledPixelX]);
                            redComponentSum += argbCalculator.getRedComponent((int) rasterizedHeightmap[coordinateY + scaledPixelY][coordinateX + scaledPixelX]);
                            greenComponentSum += argbCalculator.getGreenComponent((int) rasterizedHeightmap[coordinateY + scaledPixelY][coordinateX + scaledPixelX]);
                            blueComponentSum += argbCalculator.getBlueComponent((int) rasterizedHeightmap[coordinateY + scaledPixelY][coordinateX + scaledPixelX]);
                        }
                    }
                    final int scalePow = (int) Math.pow(absScale, 2);
                    alphaComponentSum /= scalePow;
                    redComponentSum /= scalePow;
                    greenComponentSum /= scalePow;
                    blueComponentSum /= scalePow;

                    scaledMap[coordinateY][coordinateX] = argbCalculator.compose(alphaComponentSum, redComponentSum, greenComponentSum, blueComponentSum);
                }
            }

            return scaledMap;
        }
    }

}