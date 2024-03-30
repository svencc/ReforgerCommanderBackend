package com.recom.event.event.async.map.addmappackage;

import com.recom.dto.map.scanner.structure.TransactionalMapStructurePackageDto;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class AddMapPackageAsyncEvent extends AddPackageAsyncEventBase<TransactionalMapStructurePackageDto> {

    public AddMapPackageAsyncEvent(final @NonNull TransactionalMapStructurePackageDto transactionalMapEntityPackage) {
        super(transactionalMapEntityPackage);
    }

}
