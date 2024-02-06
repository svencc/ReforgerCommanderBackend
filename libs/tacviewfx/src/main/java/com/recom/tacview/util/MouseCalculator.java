package com.recom.tacview.util;

import javafx.scene.input.MouseEvent;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MouseCalculator {

    public static double calculateRadiantBetweenMouseEvents(
            @NonNull final MouseEvent dragSource,
            @NonNull final MouseEvent event
    ) {
        return com.recom.commons.calculator.TrigonometricCalculator.calculateRadiantBetweenPoints(
                dragSource.getX(),
                dragSource.getY(),
                event.getX(),
                event.getY()
        );
    }

    public static double calculateDistanceBetweenMouseEvents(
            @NonNull final MouseEvent dragSource,
            @NonNull final MouseEvent event
    ) {
        return com.recom.commons.calculator.TrigonometricCalculator.calculateDistanceBetweenPoints(
                dragSource.getX(),
                dragSource.getY(),
                event.getX(),
                event.getY()
        );
    }

}
