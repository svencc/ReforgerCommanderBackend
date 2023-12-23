package com.recom.commander.event;

import lombok.NonNull;
import org.slf4j.Logger;
import org.springframework.context.ApplicationEvent;

public class InitializeComponentsEvent extends ApplicationEvent {

    public InitializeComponentsEvent(@NonNull final Object source) {
        super(source);
    }

    public <T> void logComponentInitialization(
            @NonNull final Logger log,
            @NonNull final Class<T> clazz
    ) {
        log.info("| +- InitializeComponentsEvent {}", clazz.getSimpleName());
    }

}
