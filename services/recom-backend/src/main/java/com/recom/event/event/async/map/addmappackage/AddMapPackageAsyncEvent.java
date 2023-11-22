package com.recom.event.event.async.map.addmappackage;

import com.recom.dto.map.scanner.structure.TransactionalMapStructureEntityPackageDto;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class AddMapPackageAsyncEvent extends AddPackageAsyncEventBase<TransactionalMapStructureEntityPackageDto> {

    public AddMapPackageAsyncEvent(final @NonNull TransactionalMapStructureEntityPackageDto transactionalMapEntityPackage) {
        super(transactionalMapEntityPackage);
    }

}
