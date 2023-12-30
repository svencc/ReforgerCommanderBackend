package com.recom.commander.service.map.overview.data;

import com.recom.commander.exception.exceptions.http.HttpErrorException;
import com.recom.dto.map.MapOverviewDto;
import com.recom.observer.BufferedSubject;
import com.recom.observer.HasBufferedSubject;
import com.recom.observer.Notification;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MapsOverviewService implements HasBufferedSubject<MapOverviewDto> {

    @NonNull
    private final AsyncTaskExecutor asyncTaskExecutor;
    @NonNull
    private final MapsOverviewGateway mapsOverviewGateway;
    @NonNull
    private final BufferedSubject<MapOverviewDto> subject = new BufferedSubject<>();


    public void reloadMapOverview() {
        asyncTaskExecutor.submit(() -> {
            try {
                final MapOverviewDto mapOverview = mapsOverviewGateway.provideMapOverview();
                subject.notifyObserversWith(Notification.of(mapOverview));
            } catch (final HttpErrorException httpE) {
                log.error("Failed to reload map overview", httpE);
            }
        });
    }

    @NonNull
    @Override
    public BufferedSubject<MapOverviewDto> getBufferedSubject() {
        return subject;
    }

}
