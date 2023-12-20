package com.recom.observer;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReactiveObserver<T> extends ObserverTemplate<T> {

    @NonNull
    private final TakeNoticeRunnable<T> reaction;

    public static <T> ReactiveObserver<T> reactWith(@NonNull final TakeNoticeRunnable<T> runnableReaction) {
        return new ReactiveObserver<T>(runnableReaction);
    }

    @Override
    public void takeNotice(
            @NonNull final Subjective<T> subject,
            @NonNull final Notification<T> notification
    ) {
        reaction.run(subject, notification);
    }

}
