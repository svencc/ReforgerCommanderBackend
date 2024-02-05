package com.recom.rendertools.rasterizer;

public class ScalingTool {

    public static double scaledPixelDimension(
            final int originalDimension,
            final int scaleFactor
    ) {
        if (scaleFactor > 0) {
            return originalDimension * scaleFactor;
        } else {
            return originalDimension / (double) Math.abs(scaleFactor);
        }
    }

    public static double scaledPixelDimension(
            final double originalDimension,
            final int scaleFactor
    ) {
        if (scaleFactor > 0) {
            return originalDimension * scaleFactor;
        } else {
            return originalDimension / (double) Math.abs(scaleFactor);
        }
    }

}