package com.recom.commander.enginemodule.entity.recommapentity.component;

import com.recom.commander.util.MapUICalculator;
import com.recom.commons.rasterizer.HeightMapDescriptor;
import com.recom.commons.units.PixelCoordinate;
import com.recom.commons.units.PixelDimension;
import com.recom.commons.units.ScaleFactor;
import com.recom.commons.units.calc.ScalingTool;
import com.recom.tacview.engine.ecs.component.PhysicCoreComponent;
import com.recom.tacview.engine.graphics.buffer.PixelBuffer;
import com.recom.tacview.engine.input.NanoTimedEvent;
import javafx.scene.input.ScrollEvent;
import lombok.NonNull;
import lombok.experimental.UtilityClass;


@UtilityClass
public class RECOMUICommands {

    public void updateMap(@NonNull final RECOMMapComponent mapComponent) {
        if (mapComponent.mapScaleFactor.getScaleFactor() == 1) {
            setUnscaledMap(mapComponent);
        } else {
            setScaledMap(mapComponent);
        }
    }

    public void setUnscaledMap(@NonNull final RECOMMapComponent mapComponent) {
        mapComponent.maybeHeightMapDescriptor.ifPresent((final HeightMapDescriptor heightMapDescriptor) -> {
            final int mapWidth = heightMapDescriptor.getHeightMap().length;
            final int mapHeight = heightMapDescriptor.getHeightMap()[0].length;

            final int[] pixelBufferArray = mapComponent.heightmapRasterizer.rasterizeHeightMapRGB(heightMapDescriptor);

            final PixelBuffer newPixelBuffer = new PixelBuffer(PixelDimension.of(mapWidth, mapHeight), pixelBufferArray);
            mapComponent.setPixelBuffer(newPixelBuffer);

            mapComponent.propagateDirtyStateToParent();
        });
    }

    public void setScaledMap(@NonNull final RECOMMapComponent mapComponent) {
        mapComponent.maybeHeightMapDescriptor.ifPresent((final HeightMapDescriptor heightMapDescriptor) -> {
            final int originalMapHeight = heightMapDescriptor.getHeightMap().length;
            final int originalMapWidth = heightMapDescriptor.getHeightMap()[0].length;

            final int scaledMapWidth = (int) ScalingTool.scaleDimension(originalMapWidth, mapComponent.mapScaleFactor.getScaleFactor());
            final int scaledMapHeight = (int) ScalingTool.scaleDimension(originalMapHeight, mapComponent.mapScaleFactor.getScaleFactor());

            final int[] newScaledPixelArray = mapComponent.heightmapRasterizer.rasterizeHeightMapRGB(heightMapDescriptor, mapComponent.mapScaleFactor.getScaleFactor());

            final PixelBuffer newScaledPixelBuffer = new PixelBuffer(PixelDimension.of(scaledMapWidth, scaledMapHeight), newScaledPixelArray);
            mapComponent.setPixelBuffer(newScaledPixelBuffer);

            mapComponent.propagateDirtyStateToParent();
        });
    }


    public void zoomInByKey(
            @NonNull final RECOMMapComponent mapComponent,
            @NonNull final PhysicCoreComponent physicsCoreComponent
    ) {
        mapComponent.maybeHeightMapDescriptor.ifPresent(heightMapDescriptor -> {
            final PixelCoordinate pointerCoordinateOnCanvas = MapUICalculator.getNormalizedCoordinateOnCanvas(mapComponent.engineProperties);
            final PixelCoordinate normalizedCoordinateOnMap = MapUICalculator.getNormalizedMapCoordinate(pointerCoordinateOnCanvas, physicsCoreComponent, mapComponent.mapScaleFactor);

            mapComponent.mapScaleFactor.zoomIn();
            updateMap(mapComponent);

            setNewZoomedMapPosition(pointerCoordinateOnCanvas, normalizedCoordinateOnMap, mapComponent.mapScaleFactor, physicsCoreComponent);
        });
    }

    public void zoomInByMouse(
            @NonNull final NanoTimedEvent<ScrollEvent> nanoTimedEvent,
            @NonNull final RECOMMapComponent mapComponent,
            @NonNull final PhysicCoreComponent physicsCoreComponent
    ) {
        mapComponent.maybeHeightMapDescriptor.ifPresent(heightMapDescriptor -> {
            final PixelCoordinate pointerCoordinateOnCanvas = MapUICalculator.getNormalizedMouseCoordinateOnCanvas(nanoTimedEvent, mapComponent.engineProperties);
            final PixelCoordinate normalizedCoordinateOnMap = MapUICalculator.getNormalizedMapCoordinate(pointerCoordinateOnCanvas, physicsCoreComponent, mapComponent.mapScaleFactor);


            mapComponent.mapScaleFactor.zoomIn();
            updateMap(mapComponent);

            setNewZoomedMapPosition(pointerCoordinateOnCanvas, normalizedCoordinateOnMap, mapComponent.mapScaleFactor, physicsCoreComponent);
        });
    }

    public void zoomOutByMouse(
            @NonNull final NanoTimedEvent<ScrollEvent> nanoTimedEvent,
            @NonNull final RECOMMapComponent mapComponent,
            @NonNull final PhysicCoreComponent physicsCoreComponent
    ) {
        mapComponent.maybeHeightMapDescriptor.ifPresent(heightMapDescriptor -> {
            final PixelCoordinate pointerCoordinateOnCanvas = MapUICalculator.getNormalizedMouseCoordinateOnCanvas(nanoTimedEvent, mapComponent.engineProperties);
            final PixelCoordinate normalizedCoordinateOnMap = MapUICalculator.getNormalizedMapCoordinate(pointerCoordinateOnCanvas, physicsCoreComponent, mapComponent.mapScaleFactor);


            mapComponent.mapScaleFactor.zoomOut();
            updateMap(mapComponent);

            setNewZoomedMapPosition(pointerCoordinateOnCanvas, normalizedCoordinateOnMap, mapComponent.mapScaleFactor, physicsCoreComponent);
        });
    }

    public void zoomOutByKey(
            @NonNull final RECOMMapComponent mapComponent,
            @NonNull final PhysicCoreComponent physicsCoreComponent
    ) {
        mapComponent.maybeHeightMapDescriptor.ifPresent(heightMapDescriptor -> {
            final PixelCoordinate pointerCoordinateOnCanvas = MapUICalculator.getNormalizedCoordinateOnCanvas(mapComponent.engineProperties);
            final PixelCoordinate normalizedCoordinateOnMap = MapUICalculator.getNormalizedMapCoordinate(pointerCoordinateOnCanvas, physicsCoreComponent, mapComponent.mapScaleFactor);

            mapComponent.mapScaleFactor.zoomOut();
            updateMap(mapComponent);

            setNewZoomedMapPosition(pointerCoordinateOnCanvas, normalizedCoordinateOnMap, mapComponent.mapScaleFactor, physicsCoreComponent);
        });
    }

    /**
     * This method is called when the user scrolls the mouse wheel. It calculates the new zoomed map position and updates the map scale.
     *
     * @param pointerCoordinateOnCanvas the pointer coordinate on canvas
     * @param normalizedCoordinateOnMap the normalized coordinate on map
     * @param mapScale                  the map scale
     * @param physicsCoreComponent      the physics core component
     */
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
