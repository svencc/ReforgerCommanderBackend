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

            setNativeMap(heightMapDescriptor);
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

            final HeightMapDescriptor heightMapDescriptor = maybeHeightMapDescriptor.get();
            final int originalMapHeight = heightMapDescriptor.getHeightMap().length;
            final int originalMapWidth = heightMapDescriptor.getHeightMap().length;

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
                setNativeMap(heightMapDescriptor);
            } else {
                setScaledMap(heightMapDescriptor, originalMapWidth, originalMapHeight);
            }


            double scaledMousePositionOnMapX = scaledPixelDimension(mousePositionOnMapX, scaleFactor);
            double scaledMousePositionOnMapY = scaledPixelDimension(mousePositionOnMapY, scaleFactor);

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


            final HeightMapDescriptor heightMapDescriptor = maybeHeightMapDescriptor.get();
            final int originalMapHeight = heightMapDescriptor.getHeightMap().length;
            final int originalMapWidth = heightMapDescriptor.getHeightMap().length;

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
                setNativeMap(heightMapDescriptor);
            } else {
                setScaledMap(heightMapDescriptor, originalMapWidth, originalMapHeight);
            }


            double scaledMousePositionOnMapX = scaledPixelDimension(mousePositionOnMapX, scaleFactor);
            double scaledMousePositionOnMapY = scaledPixelDimension(mousePositionOnMapY, scaleFactor);

            double newPositionX = 0 - scaledMousePositionOnMapX + (mousePositionOnMapX);
            double newPositionY = 0 - scaledMousePositionOnMapY + (mousePositionOnMapY);
            physicsCoreComponent.setPositionX(newPositionX);
            physicsCoreComponent.setPositionY(newPositionY);
        }
    }

    private void setNativeMap(@NonNull final HeightMapDescriptor heightMapDescriptor) {
        final int[] pixelBufferArray = heightmapRasterizer.rasterizeHeightMapRGB(heightMapDescriptor);
        final PixelDimension dimension = PixelDimension.of(heightMapDescriptor.getHeightMap().length, heightMapDescriptor.getHeightMap()[0].length);
        this.pixelBuffer = new PixelBuffer(dimension, pixelBufferArray);

        propagateDirtyStateToParent();
    }

    private void setScaledMap(
            @NonNull HeightMapDescriptor heightMapDescriptor,
            final int originalMapWidth,
            final int originalMapHeight
    ) {
        final int[] newScaledPixelArray = heightmapRasterizer.rasterizeHeightMapRGB(heightMapDescriptor, scaleFactor);
        final PixelBuffer newScaledPixelBuffer = new PixelBuffer(PixelDimension.of((int) scaledPixelDimension(originalMapWidth, scaleFactor), (int) scaledPixelDimension(originalMapHeight, scaleFactor)), newScaledPixelArray);
        this.setPixelBuffer(newScaledPixelBuffer);

        propagateDirtyStateToParent();
    }

    private double scaledPixelDimension(
            final int originalDimension,
            final int scaleFactor
    ) {
        if (scaleFactor > 0) {
            return originalDimension * scaleFactor;
        } else {
            return originalDimension / (double) Math.abs(scaleFactor);
        }
    }

    private double scaledPixelDimension(
            final double originalDimension,
            final int scaleFactor
    ) {
        if (scaleFactor > 0) {
            return originalDimension * scaleFactor;
        } else {
            return originalDimension / (double) Math.abs(scaleFactor);
        }
    }

}
