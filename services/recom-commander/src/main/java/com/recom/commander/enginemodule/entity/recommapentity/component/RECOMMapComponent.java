package com.recom.commander.enginemodule.entity.recommapentity.component;

import com.recom.commander.event.InitialAuthenticationEvent;
import com.recom.commander.mapper.HeightMapDescriptorMapper;
import com.recom.commander.service.map.overview.data.MapsOverviewService;
import com.recom.commander.service.map.topography.data.MapTopographyDataService;
import com.recom.dto.map.MapOverviewDto;
import com.recom.dto.map.topography.HeightMapDescriptorDto;
import com.recom.dto.map.topography.MapTopographyRequestDto;
import com.recom.observer.Notification;
import com.recom.observer.ReactiveObserver;
import com.recom.observer.Subjective;
import com.recom.observer.TakeNoticeRunnable;
import com.recom.rendertools.rasterizer.HeightMapDescriptor;
import com.recom.rendertools.rasterizer.HeightmapRasterizer;
import com.recom.rendertools.rasterizer.ScalingTool;
import com.recom.tacview.engine.entitycomponentsystem.component.PhysicCoreComponent;
import com.recom.tacview.engine.entitycomponentsystem.component.RenderableComponent;
import com.recom.tacview.engine.graphics.buffer.PixelBuffer;
import com.recom.tacview.engine.input.NanoTimedEvent;
import com.recom.tacview.engine.units.PixelDimension;
import jakarta.annotation.Nullable;
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
    private Optional<HeightMapDescriptor> maybeHeightMapDescriptor = Optional.empty();
    @Nullable
    private final ReactiveObserver<MapOverviewDto> mapOverviewReactiveObserver;
    @Nullable
    private final ReactiveObserver<HeightMapDescriptorDto> mapTopographyDataReactiveObserver;
    private int scaleFactor = 1;


    public RECOMMapComponent(
            @NonNull final MapsOverviewService mapsOverviewService,
            @NonNull final MapTopographyDataService mapTopographyDataService,
            @NonNull final HeightmapRasterizer heightmapRasterizer
    ) {
        super();
        this.mapsOverviewService = mapsOverviewService;
        this.mapTopographyDataService = mapTopographyDataService;
        this.heightmapRasterizer = heightmapRasterizer;
        this.setZIndex(0);

        mapOverviewReactiveObserver = ReactiveObserver.reactWith(onReloadMapOverviewReaction());
        mapOverviewReactiveObserver.observe(mapsOverviewService.getBufferedSubject());

        mapTopographyDataReactiveObserver = ReactiveObserver.reactWith(onReloadMapTopographyDataReaction());
        mapTopographyDataReactiveObserver.observe(mapTopographyDataService.getBufferedSubject());
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

    public void zoomIn(
            @NonNull final NanoTimedEvent<ScrollEvent> nanoTimedEvent,
            @NonNull final PhysicCoreComponent physicsCoreComponent
    ) {
        if (maybeHeightMapDescriptor.isPresent()) {
            final double originXOld = physicsCoreComponent.getPositionX();
            final double originYOld = physicsCoreComponent.getPositionY();
            final double scaleFactorOld = (scaleFactor > 0) ? scaleFactor : 1 / (double) Math.abs(scaleFactor);
            final double mousePositionOnMapX = (-1 * originXOld + nanoTimedEvent.getEvent().getSceneX()) / scaleFactorOld;
            final double mousePositionOnMapY = (-1 * originYOld + nanoTimedEvent.getEvent().getSceneY()) / scaleFactorOld;

            switch (scaleFactor) {
                case -2:
                    scaleFactor = 1;
                    break;
                case -1, 1:
                    scaleFactor = 2;
                    break;
                default:
                    scaleFactor++;
                    break;
            }

            if (scaleFactor == 1) {
                setMap(maybeHeightMapDescriptor.get());
            } else {
                setScaledMap(maybeHeightMapDescriptor.get(), scaleFactor);
            }


            double scaledMousePositionOnMapX = ScalingTool.scaledPixelDimension(mousePositionOnMapX, scaleFactor);
            double scaledMousePositionOnMapY = ScalingTool.scaledPixelDimension(mousePositionOnMapY, scaleFactor);

            double newPositionX = 0 - scaledMousePositionOnMapX + (mousePositionOnMapX);
            double newPositionY = 0 - scaledMousePositionOnMapY + (mousePositionOnMapY);
            physicsCoreComponent.setPositionX(newPositionX);
            physicsCoreComponent.setPositionY(newPositionY);
        }
    }

    public void zoomOut(
            @NonNull final NanoTimedEvent<ScrollEvent> nanoTimedEvent,
            @NonNull final PhysicCoreComponent physicsCoreComponent
    ) {
        if (maybeHeightMapDescriptor.isPresent()) {
            final double originXOld = physicsCoreComponent.getPositionX();
            final double originYOld = physicsCoreComponent.getPositionY();
            final double scaleFactorOld = (scaleFactor > 0) ? scaleFactor : 1 / (double) Math.abs(scaleFactor);
            final double mousePositionOnMapX = (-1 * originXOld + nanoTimedEvent.getEvent().getSceneX()) / scaleFactorOld;
            final double mousePositionOnMapY = (-1 * originYOld + nanoTimedEvent.getEvent().getSceneY()) / scaleFactorOld;

            switch (scaleFactor) {
                case 2:
                    scaleFactor = 1;
                    break;
                case 1, -1:
                    scaleFactor = -2;
                    break;
                default:
                    scaleFactor--;
                    break;
            }

            if (scaleFactor == 1) {
                setMap(maybeHeightMapDescriptor.get());
            } else {
                setScaledMap(maybeHeightMapDescriptor.get(), scaleFactor);
            }


            double scaledMousePositionOnMapX = ScalingTool.scaledPixelDimension(mousePositionOnMapX, scaleFactor);
            double scaledMousePositionOnMapY = ScalingTool.scaledPixelDimension(mousePositionOnMapY, scaleFactor);

            double newPositionX = 0 - scaledMousePositionOnMapX + (mousePositionOnMapX);
            double newPositionY = 0 - scaledMousePositionOnMapY + (mousePositionOnMapY);
            physicsCoreComponent.setPositionX(newPositionX);
            physicsCoreComponent.setPositionY(newPositionY);
        }
    }

    private void setMap(@NonNull final HeightMapDescriptor heightMapDescriptor) {
        final int mapWidth = heightMapDescriptor.getHeightMap().length;
        final int mapHeight = heightMapDescriptor.getHeightMap()[0].length;

        final int[] pixelBufferArray = heightmapRasterizer.rasterizeHeightMapRGB(heightMapDescriptor);

        final PixelBuffer newPixelBuffer = new PixelBuffer(PixelDimension.of(mapWidth, mapHeight), pixelBufferArray);
        this.setPixelBuffer(newPixelBuffer);

        propagateDirtyStateToParent();
    }

    private void setScaledMap(
            @NonNull HeightMapDescriptor heightMapDescriptor,
            final int scaleFactor
    ) {
        final int originalMapHeight = heightMapDescriptor.getHeightMap().length;
        final int originalMapWidth = heightMapDescriptor.getHeightMap()[0].length;

        final int scaledMapWidth = (int) ScalingTool.scaledPixelDimension(originalMapWidth, scaleFactor);
        final int scaledMapHeight = (int) ScalingTool.scaledPixelDimension(originalMapHeight, scaleFactor);

        final int[] newScaledPixelArray = heightmapRasterizer.rasterizeHeightMapRGB(heightMapDescriptor, scaleFactor);

        final PixelBuffer newScaledPixelBuffer = new PixelBuffer(PixelDimension.of(scaledMapWidth, scaledMapHeight), newScaledPixelArray);
        this.setPixelBuffer(newScaledPixelBuffer);

        propagateDirtyStateToParent();
    }


}
