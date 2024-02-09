package com.recom.commander.util;

import com.recom.commons.math.Round;
import com.recom.commons.units.PixelCoordinate;
import com.recom.commons.units.ScaleFactor;
import com.recom.commons.units.calc.ScalingTool;
import com.recom.tacview.engine.ecs.component.PhysicCoreComponent;
import com.recom.tacview.engine.input.NanoTimedEvent;
import com.recom.tacview.property.IsEngineProperties;
import javafx.scene.input.ScrollEvent;
import lombok.NonNull;
import lombok.experimental.UtilityClass;


@UtilityClass
public class MapCalculator {

    @NonNull
    public PixelCoordinate getCoordinateOfMouseOnMap(
            @NonNull final NanoTimedEvent<ScrollEvent> nanoTimedEvent,
            @NonNull final PhysicCoreComponent physicsCoreComponent,
            @NonNull final ScaleFactor scaleFactor,
            @NonNull final IsEngineProperties engineProperties
    ) {
        final double originX = physicsCoreComponent.getPositionX();
        final double originY = physicsCoreComponent.getPositionY();

        final double mouseOnCanvasX = applyRenderScale(nanoTimedEvent.getEvent().getSceneX(), engineProperties);
        final double mouseOnCanvasY = applyRenderScale(nanoTimedEvent.getEvent().getSceneY(), engineProperties);

        final int scaledMousePositionOnScaledMapX = (int) (-1 * originX + mouseOnCanvasX);
        final int scaledMousePositionOnScaledMapY = (int) (-1 * originY + mouseOnCanvasY);

        final int normalizedMousePositionOnNormalizedMapX = Round.halfUp(ScalingTool.normalizeDimension(scaledMousePositionOnScaledMapX, scaleFactor.getScaleFactor()));
        final int normalizedMousePositionOnNormalizedMapY = Round.halfUp(ScalingTool.normalizeDimension(scaledMousePositionOnScaledMapY, scaleFactor.getScaleFactor()));

        return PixelCoordinate.of(normalizedMousePositionOnNormalizedMapX, normalizedMousePositionOnNormalizedMapY);
    }

    public double applyRenderScale(
            final int dimension,
            @NonNull final IsEngineProperties engineProperties
    ) {
        return dimension / (double) engineProperties.getRendererScale();
    }

    public double applyRenderScale(
            final double dimension,
            @NonNull final IsEngineProperties engineProperties
    ) {
        return dimension / (double) engineProperties.getRendererScale();
    }

}
