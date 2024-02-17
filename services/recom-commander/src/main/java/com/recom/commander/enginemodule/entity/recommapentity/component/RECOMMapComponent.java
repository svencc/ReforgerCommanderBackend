package com.recom.commander.enginemodule.entity.recommapentity.component;

import com.recom.commander.event.InitialAuthenticationEvent;
import com.recom.commander.mapper.HeightMapDescriptorMapper;
import com.recom.commander.service.map.overview.data.MapsOverviewService;
import com.recom.commander.service.map.topography.data.MapTopographyDataService;
import com.recom.commander.util.MapCalculator;
import com.recom.commons.rasterizer.HeightMapDescriptor;
import com.recom.commons.rasterizer.HeightmapRasterizer;
import com.recom.commons.units.PixelCoordinate;
import com.recom.commons.units.PixelDimension;
import com.recom.commons.units.ScaleFactor;
import com.recom.commons.units.calc.ScalingTool;
import com.recom.dto.map.MapOverviewDto;
import com.recom.dto.map.topography.HeightMapDescriptorDto;
import com.recom.dto.map.topography.MapTopographyRequestDto;
import com.recom.observer.Notification;
import com.recom.observer.ReactiveObserver;
import com.recom.observer.Subjective;
import com.recom.observer.TakeNoticeRunnable;
import com.recom.tacview.engine.ecs.component.PhysicCoreComponent;
import com.recom.tacview.engine.ecs.component.RenderableComponent;
import com.recom.tacview.engine.graphics.buffer.PixelBuffer;
import com.recom.tacview.engine.input.NanoTimedEvent;
import com.recom.tacview.property.IsEngineProperties;
import jakarta.annotation.Nullable;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class RECOMMapComponent extends RenderableComponent implements AutoCloseable {

    @NonNull
    private final MapsOverviewService mapsOverviewService;
    @NonNull
    private final MapTopographyDataService mapTopographyDataService;
    @NonNull
    private final HeightmapRasterizer heightmapRasterizer;
    @NonNull
    private final IsEngineProperties engineProperties;
    @Nullable
    private final ReactiveObserver<MapOverviewDto> mapOverviewReactiveObserver;
    @Nullable
    private final ReactiveObserver<HeightMapDescriptorDto> mapTopographyDataReactiveObserver;
    @NonNull
    private final ScaleFactor mapScale = new ScaleFactor(-5, 10);
    @NonNull
    private Optional<HeightMapDescriptor> maybeHeightMapDescriptor = Optional.empty();


    public RECOMMapComponent(
            @NonNull final MapsOverviewService mapsOverviewService,
            @NonNull final MapTopographyDataService mapTopographyDataService,
            @NonNull final HeightmapRasterizer heightmapRasterizer,
            @NonNull final IsEngineProperties engineProperties
    ) {
        super();
        this.mapsOverviewService = mapsOverviewService;
        this.mapTopographyDataService = mapTopographyDataService;
        this.heightmapRasterizer = heightmapRasterizer;
        this.engineProperties = engineProperties;
        this.setZIndex(0);

        mapOverviewReactiveObserver = ReactiveObserver.reactWith(onReloadMapOverviewReaction());
        mapOverviewReactiveObserver.observe(mapsOverviewService.getBufferedSubject());

        mapTopographyDataReactiveObserver = ReactiveObserver.reactWith(onReloadMapTopographyDataReaction());
        mapTopographyDataReactiveObserver.observe(mapTopographyDataService.getBufferedSubject());
    }

    @NonNull
    private TakeNoticeRunnable<MapOverviewDto> onReloadMapOverviewReaction() {
        return (
                @NonNull final Subjective<MapOverviewDto> subject,
                @NonNull final Notification<MapOverviewDto> notification
        ) -> {
            final MapOverviewDto mapOverview = notification.getPayload();
            if (!mapOverview.getMaps().isEmpty()) {
                final MapTopographyRequestDto mapTopographyRequest = MapTopographyRequestDto.builder()
                        .mapName(mapOverview.getMaps().getFirst())
                        .build();

                mapTopographyDataService.reloadMapTopographyData(mapTopographyRequest);
            }
        };
    }

    @NonNull
    private TakeNoticeRunnable<HeightMapDescriptorDto> onReloadMapTopographyDataReaction() {
        return (
                @NonNull final Subjective<HeightMapDescriptorDto> subject,
                @NonNull final Notification<HeightMapDescriptorDto> notification
        ) -> {
            log.debug("Received map topography data");
            final HeightMapDescriptorDto heightMapDescriptorDto = notification.getPayload();
            final HeightMapDescriptor heightMapDescriptor = HeightMapDescriptorMapper.INSTANCE.toModel(heightMapDescriptorDto);
            maybeHeightMapDescriptor = Optional.of(heightMapDescriptor);

            setMap(heightMapDescriptor);
        };
    }

    private void setMap(@NonNull final HeightMapDescriptor heightMapDescriptor) {
        final int mapWidth = heightMapDescriptor.getHeightMap().length;
        final int mapHeight = heightMapDescriptor.getHeightMap()[0].length;

        final int[] pixelBufferArray = heightmapRasterizer.rasterizeHeightMapRGB(heightMapDescriptor);

        final PixelBuffer newPixelBuffer = new PixelBuffer(PixelDimension.of(mapWidth, mapHeight), pixelBufferArray);
        this.setPixelBuffer(newPixelBuffer);

        propagateDirtyStateToParent();
    }

    @EventListener(InitialAuthenticationEvent.class)
    public void onInitialAuthentication(@NonNull final InitialAuthenticationEvent event) {
        // start chain reaction; begin with loading map overview
        log.debug("Initial authentication done. Start loading map overview.");
        mapsOverviewService.reloadMapOverview();
    }

    @Override
    public void close() {
        if (mapOverviewReactiveObserver != null) {
            mapOverviewReactiveObserver.close();
        }
        if (mapTopographyDataReactiveObserver != null) {
            mapTopographyDataReactiveObserver.close();
        }
    }

    public void zoomByMouseIn(
            @NonNull final NanoTimedEvent<ScrollEvent> nanoTimedEvent,
            @NonNull final PhysicCoreComponent physicsCoreComponent
    ) {
        if (maybeHeightMapDescriptor.isPresent()) {
            final PixelCoordinate mouseCoordinateOnCanvas = PixelCoordinate.of(
                    MapCalculator.applyRenderScale(nanoTimedEvent.getEvent().getSceneX(), engineProperties),
                    MapCalculator.applyRenderScale(nanoTimedEvent.getEvent().getSceneY(), engineProperties)
            );
            final PixelCoordinate normalizedCoordinateOnMap = MapCalculator.getCoordinateOfMouseOnMap(nanoTimedEvent, physicsCoreComponent, mapScale, engineProperties);

            mapScale.zoomIn();
            if (mapScale.getScaleFactor() == 1) {
                setMap(maybeHeightMapDescriptor.get());
            } else {
                setScaledMap(maybeHeightMapDescriptor.get(), mapScale.getScaleFactor());
            }

            final PixelCoordinate scaledMapCoordinate = normalizedCoordinateOnMap.scaled(mapScale.getScaleFactor());
            physicsCoreComponent.setPositionX(-scaledMapCoordinate.getX() + mouseCoordinateOnCanvas.getX());
            physicsCoreComponent.setPositionY(-scaledMapCoordinate.getY() + mouseCoordinateOnCanvas.getY());
        }
    }

    public void zoomInByKey(
            @NonNull final NanoTimedEvent<KeyEvent> nanoTimedEvent,
            @NonNull final PhysicCoreComponent physicsCoreComponent
    ) {
        if (maybeHeightMapDescriptor.isPresent()) {
            final PixelCoordinate centerCoordinateOnCanvas = PixelCoordinate.of(
                    MapCalculator.applyRenderScale(engineProperties.getRendererHeight() / 2, engineProperties),
                    MapCalculator.applyRenderScale(engineProperties.getRendererWidth() / 2, engineProperties)
            );
            final PixelCoordinate normalizedCoordinateOnMap = MapCalculator.getCoordinateOfCenterPositionOnMap(centerCoordinateOnCanvas, physicsCoreComponent, mapScale, engineProperties);

            mapScale.zoomIn();
            if (mapScale.getScaleFactor() == 1) {
                setMap(maybeHeightMapDescriptor.get());
            } else {
                setScaledMap(maybeHeightMapDescriptor.get(), mapScale.getScaleFactor());
            }

            final PixelCoordinate scaledMapCoordinate = normalizedCoordinateOnMap.scaled(mapScale.getScaleFactor());
            physicsCoreComponent.setPositionX(-scaledMapCoordinate.getX() + centerCoordinateOnCanvas.getX());
            physicsCoreComponent.setPositionY(-scaledMapCoordinate.getY() + centerCoordinateOnCanvas.getY());
        }
    }

    private void setScaledMap(
            @NonNull HeightMapDescriptor heightMapDescriptor,
            final int scaleFactor
    ) {
        final int originalMapHeight = heightMapDescriptor.getHeightMap().length;
        final int originalMapWidth = heightMapDescriptor.getHeightMap()[0].length;

        final int scaledMapWidth = (int) ScalingTool.scaleDimension(originalMapWidth, scaleFactor);
        final int scaledMapHeight = (int) ScalingTool.scaleDimension(originalMapHeight, scaleFactor);

        final int[] newScaledPixelArray = heightmapRasterizer.rasterizeHeightMapRGB(heightMapDescriptor, scaleFactor);

        final PixelBuffer newScaledPixelBuffer = new PixelBuffer(PixelDimension.of(scaledMapWidth, scaledMapHeight), newScaledPixelArray);
        this.setPixelBuffer(newScaledPixelBuffer);

        propagateDirtyStateToParent();
    }

    public void zoomOutByMouse(
            @NonNull final NanoTimedEvent<ScrollEvent> nanoTimedEvent,
            @NonNull final PhysicCoreComponent physicsCoreComponent
    ) {
        if (maybeHeightMapDescriptor.isPresent()) {
            final PixelCoordinate mouseCoordinateOnCanvas = PixelCoordinate.of(
                    MapCalculator.applyRenderScale(nanoTimedEvent.getEvent().getSceneX(), engineProperties),
                    MapCalculator.applyRenderScale(nanoTimedEvent.getEvent().getSceneY(), engineProperties)
            );
            final PixelCoordinate normalizedCoordinateOnMap = MapCalculator.getCoordinateOfMouseOnMap(nanoTimedEvent, physicsCoreComponent, mapScale, engineProperties);

            mapScale.zoomOut();

            if (mapScale.getScaleFactor() == 1) {
                setMap(maybeHeightMapDescriptor.get());
            } else {
                setScaledMap(maybeHeightMapDescriptor.get(), mapScale.getScaleFactor());
            }

            final PixelCoordinate scaledMapCoordinate = normalizedCoordinateOnMap.scaled(mapScale.getScaleFactor());
            physicsCoreComponent.setPositionX(-scaledMapCoordinate.getX() + mouseCoordinateOnCanvas.getX());
            physicsCoreComponent.setPositionY(-scaledMapCoordinate.getY() + mouseCoordinateOnCanvas.getY());
        }
    }

    public void zoomOutByKey(
            @NonNull final NanoTimedEvent<KeyEvent> nanoTimedEvent,
            @NonNull final PhysicCoreComponent physicsCoreComponent
    ) {
        if (maybeHeightMapDescriptor.isPresent()) {
            final PixelCoordinate centerCoordinateOnCanvas = PixelCoordinate.of(
                    MapCalculator.applyRenderScale(engineProperties.getRendererHeight() / 2, engineProperties),
                    MapCalculator.applyRenderScale(engineProperties.getRendererWidth() / 2, engineProperties)
            );
            final PixelCoordinate normalizedCoordinateOnMap = MapCalculator.getCoordinateOfCenterPositionOnMap(centerCoordinateOnCanvas, physicsCoreComponent, mapScale, engineProperties);

            mapScale.zoomOut();

            if (mapScale.getScaleFactor() == 1) {
                setMap(maybeHeightMapDescriptor.get());
            } else {
                setScaledMap(maybeHeightMapDescriptor.get(), mapScale.getScaleFactor());
            }

            final PixelCoordinate scaledMapCoordinate = normalizedCoordinateOnMap.scaled(mapScale.getScaleFactor());
            physicsCoreComponent.setPositionX(-scaledMapCoordinate.getX() + centerCoordinateOnCanvas.getX());
            physicsCoreComponent.setPositionY(-scaledMapCoordinate.getY() + centerCoordinateOnCanvas.getY());
        }
    }

}
