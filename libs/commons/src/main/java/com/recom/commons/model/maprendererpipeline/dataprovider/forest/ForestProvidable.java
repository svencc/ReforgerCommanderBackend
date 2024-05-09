package com.recom.commons.model.maprendererpipeline.dataprovider.forest;

import com.recom.commons.model.maprendererpipeline.dataprovider.SpacialItemsProvidable;
import lombok.NonNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ForestProvidable extends SpacialItemsProvidable<ForestItem> {

    @NonNull
    CompletableFuture<List<ForestItem>> generateFuture();

}
