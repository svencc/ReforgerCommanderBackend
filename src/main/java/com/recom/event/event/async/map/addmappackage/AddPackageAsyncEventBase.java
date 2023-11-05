package com.recom.event.event.async.map.addmappackage;

import com.recom.dto.map.scanner.TransactionalMapEntityPackage;
import com.recom.event.event.async.RecomAsyncEvent;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AddPackageAsyncEventBase<T extends TransactionalMapEntityPackage> extends RecomAsyncEvent {

    @NonNull
    private final T transactionalMapEntityPackage;

}
