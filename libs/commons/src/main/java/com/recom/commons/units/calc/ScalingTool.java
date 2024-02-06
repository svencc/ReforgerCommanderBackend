package com.recom.commons.units.calc;

public class ScalingTool {

    public static double scaleDimension(
            final int originalDimension,
            final int scaleFactor
    ) {
        if (scaleFactor > 0) {
            return originalDimension * scaleFactor;
        } else {
            return originalDimension / (double) Math.abs(scaleFactor);
        }
    }

    public static double scaleDimension(
            final double originalDimension,
            final int scaleFactor
    ) {
        if (scaleFactor > 0) {
            return originalDimension * scaleFactor;
        } else {
            return originalDimension / (double) Math.abs(scaleFactor);
        }
    }

    public static double normalizeDimension(
            final int scaledDimension,
            final int scaleFactor
    ) {
        if (scaleFactor > 0) {
            return scaledDimension / (double) scaleFactor;
        } else {
            return scaledDimension * Math.abs(scaleFactor);
        }
    }

    public static double normalizeDimension(
            final double scaledDimension,
            final int scaleFactor
    ) {
        if (scaleFactor > 0) {
            return scaledDimension / (double) scaleFactor;
        } else {
            return scaledDimension * Math.abs(scaleFactor);
        }
    }

}