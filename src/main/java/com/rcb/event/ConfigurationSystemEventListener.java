package com.rcb.event;

import com.rcb.event.event.async.configuration.someConfigurationAsyncEvent;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConfigurationSystemEventListener extends BaseEventListener {

    @Async("ConfigurationSystemExecutor")
    @EventListener(classes = someConfigurationAsyncEvent.class)
    public void handle(@NonNull final someConfigurationAsyncEvent event) {
        logEvent(event);
    }

}
