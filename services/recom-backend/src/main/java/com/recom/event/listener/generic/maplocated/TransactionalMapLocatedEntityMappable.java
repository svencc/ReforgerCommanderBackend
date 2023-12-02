package com.recom.event.listener.generic.maplocated;

import java.util.Map;

public interface TransactionalMapLocatedEntityMappable<ENTITY_TYPE extends MapLocatedEntity, DTO_TYPE extends MapLocatedDto> {

    ENTITY_TYPE toEntity(final DTO_TYPE dto);

    void init();

}
