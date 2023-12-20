package com.recom.observer;

import lombok.NonNull;

public interface TakeNoticeRunnable<T> {


    void run(
            @NonNull final Subjective<T> subject,
            @NonNull final Notification<T> notification
    );


}
