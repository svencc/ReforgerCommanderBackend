package com.recom.event.event.async.map.addmappackage;

import com.recom.dto.map.scanner.map.TransactionalMapEntityPackageDto;
import com.recom.event.event.async.RecomAsyncEvent;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AddMapPackageAsyncEvent extends RecomAsyncEvent {

    @NonNull
    private final TransactionalMapEntityPackageDto transactionalMapEntityPackageDto;

}
