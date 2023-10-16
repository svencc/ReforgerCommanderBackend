package com.recom.observer;

import lombok.NonNull;

public interface HasSubject<T> {

    @NonNull
    Subjective<T> getSubject();

}
