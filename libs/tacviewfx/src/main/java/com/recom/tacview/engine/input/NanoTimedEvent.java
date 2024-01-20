package com.recom.tacview.engine.input;

import javafx.event.Event;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class NanoTimedEvent<EVENT_TYPE extends Event> {

    @NonNull
    private final long nanos;
    @NonNull
    private final EVENT_TYPE event;


    public static <T extends Event> NanoTimedEvent<T> of(@NonNull final T event) {
        return new NanoTimedEvent<T>(System.nanoTime(), event);
    }

    public static <T extends Event> NanoTimedEvent<T> of(
            @NonNull final long timestamp,
            @NonNull final T event
    ) {
        return new NanoTimedEvent<T>(timestamp, event);
    }

}
