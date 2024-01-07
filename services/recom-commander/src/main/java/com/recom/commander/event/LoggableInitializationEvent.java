package com.recom.commander.event;

import javafx.stage.Stage;
import lombok.Getter;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.event.Level;
import org.springframework.context.ApplicationEvent;

public abstract class LoggableInitializationEvent extends ApplicationEvent {

    @Getter
    @NonNull
    private final Stage stage;

    public LoggableInitializationEvent(
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
        log.atLevel(Level.INFO).log("| +- InitializingStageEvent: {}", clazz.getSimpleName());
    }

    public <T> void logStageInitializationError(
            @NonNull final Logger log,
            @NonNull final Class<T> clazz
    ) {
        log.atLevel(Level.ERROR).log("| +- InitializingStageEvent: {}", clazz.getSimpleName());
    }

    public <T> void logStageInitializationWithMessage(
            @NonNull final Logger log,
            @NonNull final Class<T> clazz,
            @NonNull final String message
    ) {
        logStageInitializationWithMessage(log, Level.INFO, clazz, message);
    }

    public <T> void logStageInitializationWithMessage(
            @NonNull final Logger log,
            @NonNull final Level level,
            @NonNull final Class<T> clazz,
            @NonNull final String message
    ) {
        log.atLevel(level).log("| +- InitializingStageEvent: {} > {}", clazz.getSimpleName(), message);
    }

    public <T> void logStageInitializationErrorWithMessage(
            @NonNull final Logger log,
            @NonNull final Class<T> clazz,
            @NonNull final String message
    ) {
        log.atLevel(Level.ERROR).log("| +- InitializingStageEvent: {} > {}", clazz.getSimpleName(), message);
    }

}
