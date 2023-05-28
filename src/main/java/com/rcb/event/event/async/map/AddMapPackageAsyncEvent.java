package com.rcb.event.event.async.map;

import com.rcb.dto.map.scanner.EntityPackageDto;
import com.rcb.event.event.async.RefComAsyncEvent;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Date;

@Getter
@RequiredArgsConstructor
public class AddMapPackageAsyncEvent extends RefComAsyncEvent {

    @NonNull
    private final EntityPackageDto entityPackageDto;

}
