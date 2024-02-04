package com.recom.tacview.util;

import javafx.scene.input.MouseEvent;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TrigonometricCalculator {

    public static double calculateRadiantBetweenMouseEvents(
            @NonNull final MouseEvent dragSource,
            @NonNull final MouseEvent event
    ) {
        return calculateRadiantBetweenPoints(
                dragSource.getX(),
                dragSource.getY(),
                event.getX(),
                event.getY()
        );
    }

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

    public static double calculateDistanceBetweenMouseEvents(
            @NonNull final MouseEvent dragSource,
            @NonNull final MouseEvent event
    ) {
        return calculateDistanceBetweenPoints(
                dragSource.getX(),
                dragSource.getY(),
                event.getX(),
                event.getY()
        );
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
