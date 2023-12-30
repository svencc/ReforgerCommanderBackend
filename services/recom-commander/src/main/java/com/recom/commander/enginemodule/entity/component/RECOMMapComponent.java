package com.recom.commander.enginemodule.entity.component;

import com.recom.commander.service.Scheduler;
import com.recom.commander.service.map.overview.data.MapsOverviewService;
import com.recom.commander.service.map.topography.data.MapTopographyDataService;
import com.recom.dto.map.MapOverviewDto;
import com.recom.dto.map.topography.HeightMapDescriptorDto;
import com.recom.dto.map.topography.MapTopographyRequestDto;
import com.recom.observer.ReactiveObserver;
import com.recom.tacview.engine.entity.Entity;
import com.recom.tacview.engine.entity.component.MapComponent;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RECOMMapComponent extends MapComponent implements AutoCloseable {

    @NonNull
    private final MapsOverviewService mapsOverviewService;
    @NonNull
    private final MapTopographyDataService mapTopographyDataService;
    @NonNull
    private final Scheduler scheduler;

    private ReactiveObserver<MapOverviewDto> mapOverviewReactiveObserver;
    private ReactiveObserver<HeightMapDescriptorDto> mapTopographyDataReactiveObserver;

    @Setter
    @Getter
    private int layer = 0;

    @PostConstruct
    public void init() {
        // fetch map topography data when map overview is available
        mapOverviewReactiveObserver = ReactiveObserver.reactWith((subject, notification) -> {
            final MapOverviewDto mapOverview = notification.getPayload();
            if (!mapOverview.getMaps().isEmpty()) {
                final MapTopographyRequestDto mapTopographyRequest = MapTopographyRequestDto.builder()
                        .mapName(mapOverview.getMaps().getFirst())
                        .build();
                mapTopographyDataService.reloadMapTopographyData(mapTopographyRequest);
            }
        });
        mapOverviewReactiveObserver.observe(mapsOverviewService.getBufferedSubject());

        // do something with the topography data
        mapTopographyDataReactiveObserver = ReactiveObserver.reactWith((subject, notification) -> {
            final HeightMapDescriptorDto heightMapDescriptor = notification.getPayload();
            log.info("Received map topography data <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        });
        mapTopographyDataReactiveObserver.observe(mapTopographyDataService.getBufferedSubject());

        // start chain reaction; begin with map overview
        scheduler.schedule(mapsOverviewService::reloadMapOverview, 1500);
    }


    @Override
    public void update(
            @NonNull final Entity entity,
            final long elapsedNanoTime
    ) {
        super.update(entity, elapsedNanoTime);
        // render the component
        // needs to talk with physics component (position, velocity)
    }

    @Override
    public void close() {
        if (mapTopographyDataReactiveObserver != null) {
            mapTopographyDataReactiveObserver.close();
        }
    }
}
