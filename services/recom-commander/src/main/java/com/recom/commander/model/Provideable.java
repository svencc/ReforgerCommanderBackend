package com.recom.commander.model;

import lombok.NonNull;

public interface Provideable<T> {

    @NonNull
    T provide();

}
