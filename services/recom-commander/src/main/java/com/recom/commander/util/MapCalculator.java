package com.recom.commander.util;

import com.recom.commons.math.Round;
import com.recom.commons.units.PixelCoordinate;
import com.recom.commons.units.ScaleFactor;
import com.recom.commons.units.calc.ScalingTool;
import com.recom.tacview.engine.ecs.component.PhysicCoreComponent;
import com.recom.tacview.engine.input.NanoTimedEvent;
import javafx.scene.input.ScrollEvent;
import lombok.NonNull;
import lombok.experimental.UtilityClass;


@UtilityClass
public class MapCalculator {

    @NonNull
    public PixelCoordinate getCoordinateOfMouseOnMap(
            @NonNull final NanoTimedEvent<ScrollEvent> nanoTimedEvent,
            @NonNull final PhysicCoreComponent physicsCoreComponent,
            @NonNull final ScaleFactor scaleFactor
    ) {
        final double originX = physicsCoreComponent.getPositionX();
        final double originY = physicsCoreComponent.getPositionY();

        final double mouseOnCanvasX = nanoTimedEvent.getEvent().getSceneX();
        final double mouseOnCanvasY = nanoTimedEvent.getEvent().getSceneY();

        final int scaledMousePositionOnScaledMapX = (int) (-1 * originX + mouseOnCanvasX);
        final int scaledMousePositionOnScaledMapY = (int) (-1 * originY + mouseOnCanvasY);

        final int normalizedMousePositionOnNormalizedMapX = Round.halfUp(ScalingTool.normalizeDimension(scaledMousePositionOnScaledMapX, scaleFactor.getScaleFactor()));
        final int normalizedMousePositionOnNormalizedMapY = Round.halfUp(ScalingTool.normalizeDimension(scaledMousePositionOnScaledMapY, scaleFactor.getScaleFactor()));

        return PixelCoordinate.of(normalizedMousePositionOnNormalizedMapX, normalizedMousePositionOnNormalizedMapY);
    }

}
