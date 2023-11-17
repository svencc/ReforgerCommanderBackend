package com.recom.event.listener.generic.generic;

import com.recom.entity.GameMap;
import lombok.NonNull;

public interface MapRelatedEntityPersistable<ENTITY_TYPE extends MapEntity> {

    @NonNull
    ENTITY_TYPE save(@NonNull final ENTITY_TYPE distinctEntities);

    @NonNull
    Integer deleteMapEntities(@NonNull final GameMap gameMap);

}
