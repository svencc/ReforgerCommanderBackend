package com.recom.commander.service.map.topography.data;

import com.recom.commander.exception.exceptions.http.HttpErrorException;
import com.recom.dto.map.topography.HeightMapDescriptorDto;
import com.recom.dto.map.topography.MapTopographyRequestDto;
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
public class MapTopographyDataService implements HasBufferedSubject<HeightMapDescriptorDto> {

    @NonNull
    private final AsyncTaskExecutor asyncTaskExecutor;
    @NonNull
    private final MapTopographyDataGateway mapTopographyDataGateway;
    @NonNull
    private final BufferedSubject<HeightMapDescriptorDto> subject = new BufferedSubject<>();


    public void reloadMapTopographyData(@NonNull final MapTopographyRequestDto mapTopographyRequest) {
        asyncTaskExecutor.submit(() -> {
            try {
                final HeightMapDescriptorDto heightMapDescriptor = mapTopographyDataGateway.provideMapTopographyData(mapTopographyRequest);
                subject.notifyObserversWith(Notification.of(heightMapDescriptor));
            } catch (final HttpErrorException httpE) {
                log.error("Failed to reload map topography data", httpE);
            }
        });
    }

    @NonNull
    @Override
    public BufferedSubject<HeightMapDescriptorDto> getBufferedSubject() {
        return subject;
    }

}
