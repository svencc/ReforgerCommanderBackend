package com.recom.observer;

import lombok.NonNull;

public interface HasObserver<T> {

    @NonNull
    Observing<T> getObserver();

}
