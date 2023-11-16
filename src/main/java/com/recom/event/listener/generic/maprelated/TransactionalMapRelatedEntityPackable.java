package com.recom.event.listener.generic.maprelated;

import com.recom.event.listener.generic.maprelated.MapRelatedDto;
import lombok.NonNull;

import java.util.List;

public interface TransactionalMapRelatedEntityPackable<DTO_TYPE extends MapRelatedDto> {

    @NonNull
    String getSessionIdentifier();

    @NonNull
    Integer getPackageOrder();

    @NonNull
    List<DTO_TYPE> getEntities();

}
