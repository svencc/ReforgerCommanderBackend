package com.recom.event.listener.generic;

import lombok.NonNull;

import java.util.List;

public interface TransactionalMapEntityPackable<DTO_TYPE extends MapLocatedDto> {

    @NonNull
    String getSessionIdentifier();

    @NonNull
    Integer getPackageOrder();

    @NonNull
    List<DTO_TYPE> getEntities();

}
