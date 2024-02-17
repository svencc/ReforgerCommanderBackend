package com.recom.observer;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class ReactiveObserver<T> extends ObserverTemplate<T> {

    @NonNull
    private final TakeNoticeRunnable<T> reaction;
    @NonNull
    private Optional<Duration> maybeDebounceDelay = Optional.empty();
    @NonNull
    private Optional<ScheduledExecutorService> maybeScheduledExecutor = Optional.empty();
    @NonNull
    private Optional<ScheduledFuture<?>> maybeLastTask = Optional.empty();


    @NonNull
    public static <T> ReactiveObserver<T> reactWith(@NonNull final TakeNoticeRunnable<T> runnableReaction) {
        return new ReactiveObserver<T>(runnableReaction);
    }

    @NonNull
    public static <T> ReactiveObserver<T> reactWith(
            @NonNull final TakeNoticeRunnable<T> runnableReaction,
            @NonNull final Duration debounceDelay
    ) {
        final ReactiveObserver<T> debouncedReactiveObserver = new ReactiveObserver<>(runnableReaction);
        debouncedReactiveObserver.maybeDebounceDelay = Optional.of(debounceDelay);
        debouncedReactiveObserver.maybeScheduledExecutor = Optional.of(Executors.newSingleThreadScheduledExecutor());

        return debouncedReactiveObserver;
    }

    @Override
    public void takeNotice(
            @NonNull final Subjective<T> subject,
            @NonNull final Notification<T> notification
    ) {
        if (maybeDebounceDelay.isEmpty()) {
            reaction.run(subject, notification);
        } else {
            // Debounced execution of the reaction
            if (maybeLastTask.isPresent()) {
                maybeLastTask.get().cancel(false);
            }

            maybeLastTask = Optional.of(
                    maybeScheduledExecutor.get().schedule(() -> {
                                reaction.run(subject, notification);
                            },
                            maybeDebounceDelay.get().toMillis(),
                            TimeUnit.MILLISECONDS
                    )
            );
        }
    }

}
