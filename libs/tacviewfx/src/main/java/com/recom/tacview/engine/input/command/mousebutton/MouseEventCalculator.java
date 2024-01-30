package com.recom.tacview.engine.input.command.mousebutton;

import javafx.scene.input.MouseEvent;
import lombok.NonNull;

public class MouseEventCalculator {

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
            final double x,
            final double y,
            final double x1,
            final double y1
    ) {
        final double deltaX = x1 - x;
        final double deltaY = y1 - y;

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
