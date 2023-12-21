package com.recom.commander.event;

import javafx.stage.Stage;
import lombok.Getter;
import lombok.NonNull;
import org.slf4j.Logger;
import org.springframework.context.ApplicationEvent;

public class InitializeStageEvent extends ApplicationEvent {

    @Getter
    @NonNull
    private final Stage stage;

    public InitializeStageEvent(
            @NonNull final Object source,
            @NonNull final Stage stage
    ) {
        super(source);
        this.stage = stage;
    }

    public <T> void logStageInitialization(
            @NonNull final Logger log,
            @NonNull final Class<T> clazz
    ) {
        log.info("| +-  InitializingStageEvent: {}", clazz.getSimpleName());
    }

    public <T> void logStageInitializationWithMessage(
            @NonNull final Logger log,
            @NonNull final Class<T> clazz,
            @NonNull final String message
    ) {
        log.info("| +-  InitializingStageEvent: {} > {}", clazz.getSimpleName(), message);
    }

}