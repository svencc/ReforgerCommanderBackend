package com.recom.commons.model.maprendererpipeline.dataprovider.structure;

import com.recom.commons.model.maprendererpipeline.dataprovider.SpacialItemsProvidable;
import lombok.NonNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface StructureProvidable extends SpacialItemsProvidable<StructureItem> {

    @NonNull
    CompletableFuture<List<StructureItem>> generateFuture();

}
