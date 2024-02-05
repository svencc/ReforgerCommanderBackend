package com.recom.rendertools.rasterizer;

import lombok.NonNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

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


}