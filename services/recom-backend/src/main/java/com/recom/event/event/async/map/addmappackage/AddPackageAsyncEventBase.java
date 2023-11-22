package com.recom.event.event.async.map.addmappackage;

import com.recom.event.listener.generic.generic.TransactionalMapEntityPackable;
import com.recom.event.event.async.RecomAsyncEvent;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AddPackageAsyncEventBase<T extends TransactionalMapEntityPackable> extends RecomAsyncEvent {

    @NonNull
    private final T transactionalMapEntityPackage;

}
