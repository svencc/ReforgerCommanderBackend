package com.recom.tacview.strategy;

import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;

@Builder
@RequiredArgsConstructor
public class SetFPSStrategy<T> {

    @NonNull
    private final T frame;

    public void execute(@NonNull final Consumer<T> consumer) {
        consumer.accept(frame);
    }

}
