package com.recom.commander.event;

import lombok.NonNull;
import org.slf4j.Logger;
import org.springframework.context.ApplicationEvent;

public class InitEvent extends ApplicationEvent {

    public InitEvent(@NonNull final Object source) {
        super(source);
    }

    public <T> void log(
            @NonNull final Logger log,
            @NonNull final Class<T> clazz
    ) {
        log.info("| +-  Initializing {}", clazz.getSimpleName());
    }
}
