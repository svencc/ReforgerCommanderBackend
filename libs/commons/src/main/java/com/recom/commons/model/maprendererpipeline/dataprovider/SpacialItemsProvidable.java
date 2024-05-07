package com.recom.commons.model.maprendererpipeline.dataprovider;

import lombok.NonNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface SpacialItemsProvidable<T extends SpacialItem> {

    @NonNull
    CompletableFuture<List<T>> generateFuture();

}
