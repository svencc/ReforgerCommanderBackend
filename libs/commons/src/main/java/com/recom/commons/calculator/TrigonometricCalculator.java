package com.recom.commons.calculator;

public class TrigonometricCalculator {

    public static double calculateRadiantBetweenPoints(
            final double x1,
            final double y1,
            final double x2,
            final double y2
    ) {
        final double deltaX = x2 - x1;
        final double deltaY = y2 - y1;

        return Math.atan2(deltaY, deltaX);
    }

    public static double calculateDistanceBetweenPoints(
            final double x1,
            final double y1,
            final double x2,
            final double y2
    ) {
        final double deltaX = x2 - x1;
        final double deltaY = y2 - y1;

        return Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
    }

}
