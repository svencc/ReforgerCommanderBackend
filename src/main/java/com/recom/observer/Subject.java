package com.recom.observer;

import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

public class Subject<T> implements Subjective<T> {

    @NonNull
    private final List<Observing<T>> observersWatchingMe = new ArrayList<>();


    @Override
    public void beObservedBy(final @NonNull Observing<T> observer) {
        observersWatchingMe.add(observer);
    }

    @Override
    public void observationStoppedThrough(final @NonNull Observing<T> observer) {
        observersWatchingMe.remove(observer);
    }

    @Override
    public void notifyObserversWith(@NonNull final Notification<T> notification) {
        // run through a copy of the list to avoid ConcurrentModificationException when removing observers!
        final List<Observing<T>> copiedObserversWatchingMe = new ArrayList<>(observersWatchingMe);
        copiedObserversWatchingMe.forEach(observer -> observer.takeNotice(this, notification));
    }

    @Override
    public void reportMyDeath() {
        observersWatchingMe.forEach(observer -> observer.takeDeathNoticeFrom(this));
    }

}
