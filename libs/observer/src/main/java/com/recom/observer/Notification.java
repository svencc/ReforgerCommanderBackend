package com.recom.observer;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Notification<T> {

    @NonNull
    private final T payload;

    public static <T> Notification<T> of(@NonNull final T payload) {
        return new Notification<>(payload);
    }

    @NonNull
    public T getPayload() {
        return payload;
    }

}
