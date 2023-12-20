package com.recom.observer;

import lombok.NonNull;
import org.springframework.lang.Nullable;

import java.util.Optional;

public class BufferedSubject<T> extends Subject<T> implements Buffered<T> {

    @Nullable
    private Notification<T> lastNotificationBuffer;

    @NonNull
    @Override
    public Optional<Notification<T>> getLastBufferedNotification() {
        return Optional.ofNullable(lastNotificationBuffer);
    }

    @Override
    public void beObservedBy(final @NonNull Observing<T> observer) {
        super.beObservedBy(observer);
        if (lastNotificationBuffer != null) {
            observer.takeNotice(this, lastNotificationBuffer);
        }
    }

    @Override
    public void notifyObserversWith(@NonNull final Notification<T> notification) {
        lastNotificationBuffer = notification;
        super.notifyObserversWith(notification);
    }

}
