package com.recom.service.mapentitygenerator;

import com.recom.commons.model.maprendererpipeline.dataprovider.SpacialItem;
import com.recom.commons.model.maprendererpipeline.dataprovider.SpacialItemProvidable;
import com.recom.entity.map.GameMap;
import lombok.NonNull;

public interface SpacialItemProviderGenerator<T extends SpacialItemProvidable<? extends SpacialItem>> {

    @NonNull
    T generate(@NonNull final GameMap gameMap);

}
