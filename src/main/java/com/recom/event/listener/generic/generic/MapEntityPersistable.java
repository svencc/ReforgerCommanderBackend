package com.recom.event.listener.generic.generic;

import com.recom.entity.GameMap;
import lombok.NonNull;

import java.util.List;

//public interface MapLocatedEntityPersistable<ENTITY_TYPE extends MapLocatedEntity> {
public interface MapEntityPersistable<ENTITY_TYPE extends MapEntity> {

    @NonNull
    public List<ENTITY_TYPE> saveAll(@NonNull final List<ENTITY_TYPE> distinctEntities);

    @NonNull
    public Integer deleteMapEntities(@NonNull final GameMap gameMap);

}
