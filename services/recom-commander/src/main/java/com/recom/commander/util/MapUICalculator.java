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
    public PixelCoordinate getMouseCoordinateOnCanvas(
            @NonNull final NanoTimedEvent<ScrollEvent> nanoTimedEvent,
            @NonNull final IsEngineProperties engineProperties
    ) {
        return PixelCoordinate.of(
                MapUICalculator.applyRenderScale(nanoTimedEvent.getEvent().getSceneX(), engineProperties),
                MapUICalculator.applyRenderScale(nanoTimedEvent.getEvent().getSceneY(), engineProperties)
        );
    }

    @NonNull
    public PixelCoordinate getCenterCoordinateOnCanvas(@NonNull final IsEngineProperties engineProperties) {
        return PixelCoordinate.of(
                engineProperties.getRendererWidth() / 2,
                engineProperties.getRendererHeight() / 2
        );
    }

    @NonNull
    public PixelCoordinate getCoordinateOfMouseOnMap(
            @NonNull final NanoTimedEvent<ScrollEvent> nanoTimedEvent,
            @NonNull final PhysicCoreComponent physicsCoreComponent,
            @NonNull final ScaleFactor scaleFactor,
            @NonNull final IsEngineProperties engineProperties
    ) {
        final double originX = physicsCoreComponent.getPositionX();
        final double originY = physicsCoreComponent.getPositionY();

        final double mouseOnNormalizedCanvasX = applyRenderScale(nanoTimedEvent.getEvent().getSceneX(), engineProperties);
        final double mouseOnNormalizedCanvasY = applyRenderScale(nanoTimedEvent.getEvent().getSceneY(), engineProperties);

        final int scaledMousePositionOnScaledMapX = (int) (-1 * originX + mouseOnNormalizedCanvasX);
        final int scaledMousePositionOnScaledMapY = (int) (-1 * originY + mouseOnNormalizedCanvasY);

        final int normalizedMousePositionOnNormalizedMapX = Round.halfUp(ScalingTool.normalizeDimension(scaledMousePositionOnScaledMapX, scaleFactor.getScaleFactor()));
        final int normalizedMousePositionOnNormalizedMapY = Round.halfUp(ScalingTool.normalizeDimension(scaledMousePositionOnScaledMapY, scaleFactor.getScaleFactor()));

        return PixelCoordinate.of(normalizedMousePositionOnNormalizedMapX, normalizedMousePositionOnNormalizedMapY);
    }

    @NonNull
    public PixelCoordinate getCoordinateOfCenterPositionOnMap(
            @NonNull final PixelCoordinate centerPosition,
            @NonNull final PhysicCoreComponent physicsCoreComponent,
            @NonNull final ScaleFactor scaleFactor
    ) {
        final double originX = physicsCoreComponent.getPositionX();
        final double originY = physicsCoreComponent.getPositionY();

        final double positionOnNormalizedCanvasX = centerPosition.getX();
        final double positionOnNormalizedCanvasY = centerPosition.getY();

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

    public void setNewZoomedMapPosition(
            @NonNull final PixelCoordinate pointerCoordinateOnCanvas,
            @NonNull final PixelCoordinate normalizedCoordinateOnMap,
            @NonNull final ScaleFactor mapScale,
            @NonNull final PhysicCoreComponent physicsCoreComponent
    ) {
        final PixelCoordinate scaledMapCoordinate = normalizedCoordinateOnMap.scaled(mapScale.getScaleFactor());
        physicsCoreComponent.setPositionX(-scaledMapCoordinate.getX() + pointerCoordinateOnCanvas.getX());
        physicsCoreComponent.setPositionY(-scaledMapCoordinate.getY() + pointerCoordinateOnCanvas.getY());
    }
}
