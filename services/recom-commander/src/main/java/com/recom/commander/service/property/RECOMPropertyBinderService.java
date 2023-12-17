package com.recom.commander.service.property;

import com.recom.dynamicproperties.DynamicProperties;
import com.recom.dynamicproperties.PropertyFileBinder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;


@Slf4j
@Service
@RequiredArgsConstructor
public class RECOMPropertyBinderService {

    @NonNull
    private final PropertyFileBinder propertyFileBinder;
    @NonNull
    private final AsyncTaskExecutor asyncTaskExecutor;


    @NonNull
    public <T extends DynamicProperties> T bind(@NonNull final T properties) {
        propertyFileBinder.bindToFilesystem(properties);
        bindWatchService(properties);

        return properties;
    }

    // TODO: Exception Handling und Logging beim Binder; richtig fehler loggen; Applikation abbrechen?
    @SneakyThrows(IOException.class)
    private void bindWatchService(@NonNull final DynamicProperties properties) {
        final WatchService watchService = FileSystems.getDefault().newWatchService();
        properties.getPropertiesBasePath().resolve(properties.getApplicationName()).register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.OVERFLOW);

        final String pathToPropertyFileString = String.format("%1s.properties", properties.getPropertyFileName());
        asyncTaskExecutor.execute(() -> {
            WatchKey watchKey;
            try {
                while ((watchKey = watchService.take()) != null) {
                    watchKey.pollEvents().forEach(watchEvent -> {
                        if (watchEvent.context().toString().equalsIgnoreCase(pathToPropertyFileString)) {
                            log.info("Reloaded properties from filesystem: '{}'", pathToPropertyFileString);
                            properties.load(); // trigger reload once AFTER all events
                        }
                    });

                    watchKey.reset();
                }
            } catch (final InterruptedException e) {
                log.warn("WatchService interrupted", e);
            }
        });
    }

}
