package com.recom.observer;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Notification<T> {

    @NonNull
    private final T payload;

    @NonNull
    public T getPayload() {
        return payload;
    }

}
