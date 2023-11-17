package com.recom.event.listener.generic.generic;

import com.recom.entity.GameMap;
import com.recom.event.listener.generic.maplocated.MapLocatedEntity;
import lombok.NonNull;

import java.util.List;

public interface MapLocatedEntityPersistable<ENTITY_TYPE extends MapLocatedEntity> {

    @NonNull
    List<ENTITY_TYPE> saveAll(@NonNull final List<ENTITY_TYPE> distinctEntities);

    @NonNull
    Integer deleteMapEntities(@NonNull final GameMap gameMap);

}
