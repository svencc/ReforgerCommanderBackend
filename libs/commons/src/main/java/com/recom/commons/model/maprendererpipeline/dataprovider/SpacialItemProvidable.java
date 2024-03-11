package com.recom.commons.model.maprendererpipeline.dataprovider;

import lombok.NonNull;

import java.util.List;

public interface SpacialItemProvidable<T extends SpacialItem> {

    @NonNull
    List<T> provide();

}
