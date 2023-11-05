package com.recom.event.listener.generic;

public interface TransactionalMapEntityMappable<ENTITY_TYPE extends MapLocatedEntity, DTO_TYPE extends MapLocatedDto> {

    ENTITY_TYPE toEntity(final DTO_TYPE dto);

}
