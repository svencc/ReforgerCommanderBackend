package com.recom.event.event.async.map;

import com.recom.dto.map.scanner.TransactionalEntityPackageDto;
import com.recom.event.event.async.RecomAsyncEvent;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AddMapPackageAsyncEvent extends RecomAsyncEvent {

    @NonNull
    private final TransactionalEntityPackageDto transactionalEntityPackageDto;

}
