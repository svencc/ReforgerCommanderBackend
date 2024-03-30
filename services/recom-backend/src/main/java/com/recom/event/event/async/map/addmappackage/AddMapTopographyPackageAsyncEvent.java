package com.recom.event.event.async.map.addmappackage;

import com.recom.dto.map.scanner.topography.TransactionalMapTopographyPackageDto;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class AddMapTopographyPackageAsyncEvent extends AddPackageAsyncEventBase<TransactionalMapTopographyPackageDto> {

    public AddMapTopographyPackageAsyncEvent(final @NonNull TransactionalMapTopographyPackageDto transactionalMapEntityPackage) {
        super(transactionalMapEntityPackage);
    }

}
