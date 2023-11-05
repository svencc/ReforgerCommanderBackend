package com.recom.event.event.async.map.addmappackage;

import com.recom.event.event.async.RecomAsyncEvent;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class AddPackageAsyncEventBase<T> extends RecomAsyncEvent {

    @NonNull
    private final T transactionalMapEntityPackage;

}
