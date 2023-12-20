package com.recom.observer;

import lombok.NonNull;

public interface HasBufferedSubject<T> {

    @NonNull
    BufferedSubject<T> getBufferedSubject();

}
