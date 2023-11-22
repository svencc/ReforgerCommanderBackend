package com.recom.event.listener.generic.maplocated;

public interface TransactionalMapLocatedEntityMappable<ENTITY_TYPE extends MapLocatedEntity, DTO_TYPE extends MapLocatedDto> {

    ENTITY_TYPE toEntity(final DTO_TYPE dto);

}
