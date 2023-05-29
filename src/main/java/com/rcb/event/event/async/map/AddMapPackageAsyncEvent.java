package com.rcb.event.event.async.map;

import com.rcb.dto.map.scanner.TransactionalEntityPackageDto;
import com.rcb.event.event.async.RefComAsyncEvent;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AddMapPackageAsyncEvent extends RefComAsyncEvent {

    @NonNull
    private final TransactionalEntityPackageDto transactionalEntityPackageDto;

}
