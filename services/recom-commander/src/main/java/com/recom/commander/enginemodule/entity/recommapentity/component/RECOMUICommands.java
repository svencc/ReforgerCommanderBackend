package com.recom.commander.enginemodule.entity.recommapentity.component;

import com.recom.commander.util.MapUICalculator;
import com.recom.commons.rasterizer.HeightMapDescriptor;
import com.recom.commons.rasterizer.HeightmapRasterizer;
import com.recom.commons.units.PixelCoordinate;
import com.recom.commons.units.PixelDimension;
import com.recom.commons.units.ScaleFactor;
import com.recom.commons.units.calc.ScalingTool;
import com.recom.tacview.engine.ecs.component.PhysicCoreComponent;
import com.recom.tacview.engine.graphics.buffer.PixelBuffer;
import com.recom.tacview.engine.input.NanoTimedEvent;
import com.recom.tacview.property.IsEngineProperties;
import javafx.scene.input.ScrollEvent;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class RECOMUICommands {

    @NonNull
    private final RECOMMapComponent mapComponent;
    @NonNull
    private final HeightmapRasterizer heightmapRasterizer;

    public void zoomInByKey(
            @NonNull final HeightMapDescriptor heightMapDescriptor,
            @NonNull final PhysicCoreComponent physicsCoreComponent,
            @NonNull final ScaleFactor mapScale,
            @NonNull final IsEngineProperties engineProperties
    ) {
        final PixelCoordinate pointerCoordinateOnCanvas = MapUICalculator.getCenterCoordinateOnCanvas(engineProperties);
        final PixelCoordinate normalizedCoordinateOnMap = MapUICalculator.getCoordinateOfCenterPositionOnMap(pointerCoordinateOnCanvas, physicsCoreComponent, mapScale);

        mapScale.zoomIn();
        updateMap(heightMapDescriptor, mapScale);

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
        updateMap(heightMapDescriptor, mapScale);

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
        updateMap(heightMapDescriptor, mapScaleFactor);

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
        updateMap(heightMapDescriptor, mapScaleFactor);

        MapUICalculator.setNewZoomedMapPosition(pointerCoordinateOnCanvas, normalizedCoordinateOnMap, mapScaleFactor, physicsCoreComponent);
    }

    void updateMap(
            @NonNull final HeightMapDescriptor heightMapDescriptor,
            @NonNull final ScaleFactor scaleFactor
    ) {
        if (scaleFactor.getScaleFactor() == 1) {
            setUnscaledMap(heightMapDescriptor);
        } else {
            setScaledMap(heightMapDescriptor, scaleFactor);
        }
    }

    public void setUnscaledMap(@NonNull final HeightMapDescriptor heightMapDescriptor) {
        final int mapWidth = heightMapDescriptor.getHeightMap().length;
        final int mapHeight = heightMapDescriptor.getHeightMap()[0].length;

        final int[] pixelBufferArray = heightmapRasterizer.rasterizeHeightMapRGB(heightMapDescriptor);

        final PixelBuffer newPixelBuffer = new PixelBuffer(PixelDimension.of(mapWidth, mapHeight), pixelBufferArray);
        mapComponent.setPixelBuffer(newPixelBuffer);

        mapComponent.propagateDirtyStateToParent();
    }

    public void setScaledMap(
            @NonNull final HeightMapDescriptor heightMapDescriptor,
            @NonNull final ScaleFactor scaleFactor
    ) {
        final int originalMapHeight = heightMapDescriptor.getHeightMap().length;
        final int originalMapWidth = heightMapDescriptor.getHeightMap()[0].length;

        final int scaledMapWidth = (int) ScalingTool.scaleDimension(originalMapWidth, scaleFactor.getScaleFactor());
        final int scaledMapHeight = (int) ScalingTool.scaleDimension(originalMapHeight, scaleFactor.getScaleFactor());

        final int[] newScaledPixelArray = heightmapRasterizer.rasterizeHeightMapRGB(heightMapDescriptor, scaleFactor.getScaleFactor());

        final PixelBuffer newScaledPixelBuffer = new PixelBuffer(PixelDimension.of(scaledMapWidth, scaledMapHeight), newScaledPixelArray);
        mapComponent.setPixelBuffer(newScaledPixelBuffer);

        mapComponent.propagateDirtyStateToParent();
    }

}
