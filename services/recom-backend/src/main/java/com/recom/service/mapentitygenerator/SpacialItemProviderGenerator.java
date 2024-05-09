package com.recom.service.mapentitygenerator;

import com.recom.commons.model.maprendererpipeline.dataprovider.SpacialItem;
import com.recom.commons.model.maprendererpipeline.dataprovider.SpacialItemsProvidable;
import com.recom.entity.map.GameMap;
import lombok.NonNull;

public interface SpacialItemProviderGenerator<T extends SpacialItemsProvidable<? extends SpacialItem>> {

    @NonNull
    T provideFutureGenerator(@NonNull final GameMap gameMap);

}
