package com.recom.dto.map.scanner;

import lombok.NonNull;

import java.util.List;

public interface TransactionalEntityPackage<T> {

    @NonNull
    String getSessionIdentifier();

    @NonNull
    Integer getPackageOrder();

    @NonNull
    List<T> getEntities();

}
