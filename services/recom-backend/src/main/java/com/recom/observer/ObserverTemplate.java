package com.recom.observer;

import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

public abstract class ObserverTemplate<T> implements Observing<T>, AutoCloseable {

    @NonNull
    protected final List<Subjective<T>> subjects = new ArrayList<>();

    @Override
    public void observe(@NonNull final Subjective<T> subject) {
        subject.beObservedBy(this);
        subjects.add(subject);
    }

    @Override
    public abstract void takeNotice(
            @NonNull final Subjective<T> subject,
            @NonNull final Notification<T> notification
    );

    @Override
    public void takeDeathNoticeFrom(@NonNull final Subjective<T> subject) {
        subjects.remove(subject);
    }

    @Override
    public void close() {
        subjects.forEach(subject -> subject.observationStoppedThrough(this));
    }

}
