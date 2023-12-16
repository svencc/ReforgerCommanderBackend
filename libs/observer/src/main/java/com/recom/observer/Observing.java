package com.recom.observer;

import lombok.NonNull;

public interface Observing<T> {

    void observe(@NonNull final Subjective<T> subject);

    void takeNotice(
            @NonNull final Subjective<T> subject,
            @NonNull final Notification<T> notification
    );

    void takeDeathNoticeFrom(@NonNull final Subjective<T> subject);

}
