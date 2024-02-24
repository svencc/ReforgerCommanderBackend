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
public class MapUICalculator {

    @NonNull
    public PixelCoordinate getNormalizedMouseCoordinateOnCanvas(
            @NonNull final NanoTimedEvent<ScrollEvent> nanoTimedEvent,
            @NonNull final IsEngineProperties engineProperties
    ) {
        return PixelCoordinate.of(
                MapUICalculator.applyRenderScale(nanoTimedEvent.getEvent().getSceneX(), engineProperties),
                MapUICalculator.applyRenderScale(nanoTimedEvent.getEvent().getSceneY(), engineProperties)
        );
    }

    @NonNull
    public PixelCoordinate getNormalizedCenterCoordinateOnCanvas(@NonNull final IsEngineProperties engineProperties) {
        return PixelCoordinate.of(
                MapUICalculator.applyRenderScale(engineProperties.getRendererWidth() / 2, engineProperties),
                MapUICalculator.applyRenderScale(engineProperties.getRendererHeight() / 2, engineProperties)
        );
    }

    @NonNull
    public PixelCoordinate getNormalizedMapCoordinate(
            @NonNull final PixelCoordinate normalizedPointerPosition,
            @NonNull final PhysicCoreComponent physicsCoreComponent,
            @NonNull final ScaleFactor scaleFactor
    ) {
        final double originX = physicsCoreComponent.getPositionX();
        final double originY = physicsCoreComponent.getPositionY();

        final double positionOnNormalizedCanvasX = normalizedPointerPosition.getX();
        final double positionOnNormalizedCanvasY = normalizedPointerPosition.getY();

        final int scaledCenterPositionOnScaledMapX = (int) (-1 * originX + positionOnNormalizedCanvasX);
        final int scaledCenterPositionOnScaledMapY = (int) (-1 * originY + positionOnNormalizedCanvasY);

        final int normalizedCenterPositionOnNormalizedMapX = Round.halfUp(ScalingTool.normalizeDimension(scaledCenterPositionOnScaledMapX, scaleFactor.getScaleFactor()));
        final int normalizedCenterPositionOnNormalizedMapY = Round.halfUp(ScalingTool.normalizeDimension(scaledCenterPositionOnScaledMapY, scaleFactor.getScaleFactor()));

        return PixelCoordinate.of(normalizedCenterPositionOnNormalizedMapX, normalizedCenterPositionOnNormalizedMapY);
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
