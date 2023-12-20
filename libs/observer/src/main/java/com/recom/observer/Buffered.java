package com.recom.observer;

import lombok.NonNull;

import java.util.Optional;

public interface Buffered<T> {

    @NonNull
    Optional<Notification<T>> getLastBufferedNotification();

}
