package com.recom.observer;

import lombok.NonNull;

public interface Subjective<T> {

    void beObservedBy(@NonNull final Observing<T> observer);

    void observationStoppedThrough(@NonNull final Observing<T> observer);

    void notifyObserversWith(@NonNull final Notification<T> notification);

    void reportMyDeath();

}
