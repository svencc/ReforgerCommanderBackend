package com.recom.event.event.async.map.addmappackage;

import com.recom.dto.map.scanner.map.TransactionalMapEntityPackageDto;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class AddMapPackageAsyncEvent extends AddPackageAsyncEventBase<TransactionalMapEntityPackageDto> {

    public AddMapPackageAsyncEvent(final @NonNull TransactionalMapEntityPackageDto transactionalMapEntityPackage) {
        super(transactionalMapEntityPackage);
    }

}
