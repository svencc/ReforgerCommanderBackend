package com.recom.commander.exception;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GlobalExceptionHandler implements Thread.UncaughtExceptionHandler {

    @Override
    public void uncaughtException(
            @NonNull final Thread t,
            @NonNull final Throwable throwable
    ) {
        String errorType = "Unknown exception type";
        switch (throwable) {
            case Error error -> errorType = "Error";
            case RuntimeException runtimeException -> errorType = "Runtime exception";
            case Exception exception -> errorType = "Exception";
            default -> errorType = "Unknown exception type";
        }

        log.error("{} ({}): '{}'", errorType, throwable.getClass().getSimpleName(), throwable.getMessage(), throwable);
    }

}
