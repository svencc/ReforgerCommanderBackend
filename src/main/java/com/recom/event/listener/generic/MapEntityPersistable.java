package com.recom.event.listener.generic;

import lombok.NonNull;

import java.util.List;

public interface MapEntityPersistable<ENTITY_TYPE extends MapLocatedEntity> {

    @NonNull
    public List<ENTITY_TYPE> saveAll(@NonNull final List<ENTITY_TYPE> distinctEntities);

    @NonNull
    public Integer deleteMapEntities(@NonNull final String mapName);

}
