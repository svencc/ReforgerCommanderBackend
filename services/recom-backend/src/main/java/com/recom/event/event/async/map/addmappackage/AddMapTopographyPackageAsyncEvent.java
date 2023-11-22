package com.recom.event.event.async.map.addmappackage;

import com.recom.dto.map.scanner.topography.TransactionalMapTopographyEntityPackageDto;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class AddMapTopographyPackageAsyncEvent extends AddPackageAsyncEventBase<TransactionalMapTopographyEntityPackageDto> {

    public AddMapTopographyPackageAsyncEvent(final @NonNull TransactionalMapTopographyEntityPackageDto transactionalMapEntityPackage) {
        super(transactionalMapEntityPackage);
    }

}
