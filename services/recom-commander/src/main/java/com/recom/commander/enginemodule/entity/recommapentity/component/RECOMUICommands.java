package com.recom.commander.enginemodule.entity.recommapentity.component;

import com.recom.commander.util.MapUICalculator;
import com.recom.commons.rasterizer.HeightMapDescriptor;
import com.recom.commons.units.PixelCoordinate;
import com.recom.commons.units.ScaleFactor;
import com.recom.tacview.engine.ecs.component.PhysicCoreComponent;
import com.recom.tacview.engine.input.NanoTimedEvent;
import com.recom.tacview.property.IsEngineProperties;
import javafx.scene.input.ScrollEvent;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class RECOMUICommands {

    @NonNull
    final RECOMMapComponent mapComponent;

    public void zoomInByKey(
            @NonNull final HeightMapDescriptor heightMapDescriptor,
            @NonNull final PhysicCoreComponent physicsCoreComponent,
            @NonNull final ScaleFactor mapScale,
            @NonNull final IsEngineProperties engineProperties
    ) {
        final PixelCoordinate pointerCoordinateOnCanvas = MapUICalculator.getCenterCoordinateOnCanvas(engineProperties);
        final PixelCoordinate normalizedCoordinateOnMap = MapUICalculator.getCoordinateOfCenterPositionOnMap(pointerCoordinateOnCanvas, physicsCoreComponent, mapScale);

        mapScale.zoomIn();
        mapComponent.updateMap(heightMapDescriptor, mapScale);

        MapUICalculator.setNewZoomedMapPosition(pointerCoordinateOnCanvas, normalizedCoordinateOnMap, mapScale, physicsCoreComponent);
    }

    public void zoomInByMouse(
            @NonNull final HeightMapDescriptor heightMapDescriptor,
            @NonNull final NanoTimedEvent<ScrollEvent> nanoTimedEvent,
            @NonNull final PhysicCoreComponent physicsCoreComponent,
            @NonNull final ScaleFactor mapScale,
            @NonNull final IsEngineProperties engineProperties
    ) {
        final PixelCoordinate pointerCoordinateOnCanvas = MapUICalculator.getMouseCoordinateOnCanvas(nanoTimedEvent, engineProperties);
        final PixelCoordinate normalizedCoordinateOnMap = MapUICalculator.getCoordinateOfMouseOnMap(nanoTimedEvent, physicsCoreComponent, mapScale, engineProperties);

        mapScale.zoomIn();
        mapComponent.updateMap(heightMapDescriptor, mapScale);

        MapUICalculator.setNewZoomedMapPosition(pointerCoordinateOnCanvas, normalizedCoordinateOnMap, mapScale, physicsCoreComponent);
    }

    public void zoomOutByMouse(
            @NonNull final HeightMapDescriptor heightMapDescriptor,
            @NonNull final NanoTimedEvent<ScrollEvent> nanoTimedEvent,
            @NonNull final PhysicCoreComponent physicsCoreComponent,
            @NonNull final ScaleFactor mapScaleFactor,
            @NonNull final IsEngineProperties engineProperties
    ) {
        final PixelCoordinate pointerCoordinateOnCanvas = MapUICalculator.getMouseCoordinateOnCanvas(nanoTimedEvent, engineProperties);
        final PixelCoordinate normalizedCoordinateOnMap = MapUICalculator.getCoordinateOfMouseOnMap(nanoTimedEvent, physicsCoreComponent, mapScaleFactor, engineProperties);

        mapScaleFactor.zoomOut();
        mapComponent.updateMap(heightMapDescriptor, mapScaleFactor);

        MapUICalculator.setNewZoomedMapPosition(pointerCoordinateOnCanvas, normalizedCoordinateOnMap, mapScaleFactor, physicsCoreComponent);
    }

    public void zoomOutByKey(
            @NonNull final HeightMapDescriptor heightMapDescriptor,
            @NonNull final PhysicCoreComponent physicsCoreComponent,
            @NonNull final ScaleFactor mapScaleFactor,
            @NonNull final IsEngineProperties engineProperties
    ) {
        final PixelCoordinate pointerCoordinateOnCanvas = MapUICalculator.getCenterCoordinateOnCanvas(engineProperties);
        final PixelCoordinate normalizedCoordinateOnMap = MapUICalculator.getCoordinateOfCenterPositionOnMap(pointerCoordinateOnCanvas, physicsCoreComponent, mapScaleFactor);

        mapScaleFactor.zoomOut();
        mapComponent.updateMap(heightMapDescriptor, mapScaleFactor);

        MapUICalculator.setNewZoomedMapPosition(pointerCoordinateOnCanvas, normalizedCoordinateOnMap, mapScaleFactor, physicsCoreComponent);
    }

}
