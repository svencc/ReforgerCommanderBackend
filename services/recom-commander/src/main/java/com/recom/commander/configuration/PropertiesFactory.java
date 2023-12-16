package com.recom.commander.configuration;

import com.recom.commander.property.user.HostProperties;
import com.recom.dynamicproperties.DynamicProperties;
import com.recom.dynamicproperties.PropertyBinder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.atomic.AtomicBoolean;


@Slf4j
@Configuration
@RequiredArgsConstructor
public class PropertiesFactory {

    @NonNull
    private final PropertyBinder propertyBinder;
    @NonNull
    private final AsyncTaskExecutor asyncTaskExecutor;

    @Nullable
    private WatchService watchService;


    @Bean
    public HostProperties hostProperties() {
        return bind(new HostProperties());
    }

    @NonNull
    private <T extends DynamicProperties> T bind(@NonNull final T properties) {
        propertyBinder.bindToFilesystem(properties);
        bindWatchService(properties);

        return properties;
    }

    // TODO: Exception Handling und Logging beim Binder; richtig fehler loggen; Applikation abbrechen?
    @SneakyThrows(IOException.class)
    private void bindWatchService(@NonNull final DynamicProperties properties) {
        watchService = FileSystems.getDefault().newWatchService();
        properties.getPropertiesBasePath().resolve(properties.getApplicationName()).register(
                watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY,
                StandardWatchEventKinds.ENTRY_DELETE,
                StandardWatchEventKinds.OVERFLOW
        );

        final String pathToPropertyFileString = properties.getPropertyFileName();
        asyncTaskExecutor.execute(() -> {
            WatchKey watchKey;
            try {
                while ((watchKey = watchService.take()) != null) {
                    final AtomicBoolean propertyFileNameAffected = new AtomicBoolean(false);
                    watchKey.pollEvents().forEach(watchEvent -> {
                        if (watchEvent.context().toString().equalsIgnoreCase(properties.getPropertyFileName() + ".properties")) {
                            propertyFileNameAffected.set(true);
                        }
                        log.info("Event kind: {}. File affected: '{}'", watchEvent.kind(), watchEvent.context());
                    });

                    if (propertyFileNameAffected.get()) {
                        log.info("Reloaded properties from filesystem: '{}'", pathToPropertyFileString);
                        properties.load(); // trigger reload once AFTER all events
                    }
                    watchKey.reset();
                }
            } catch (final InterruptedException e) {
                log.warn("WatchService interrupted", e);
            }
        });
    }

}
