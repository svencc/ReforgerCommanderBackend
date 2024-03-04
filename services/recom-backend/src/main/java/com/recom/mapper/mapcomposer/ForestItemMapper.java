package com.recom.mapper.mapcomposer;

import com.recom.commons.model.maprendererpipeline.dataprovider.forest.ForestItem;
import com.recom.entity.map.structure.MapStructureEntity;
import lombok.NonNull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ForestItemMapper {

    ForestItemMapper INSTANCE = Mappers.getMapper(ForestItemMapper.class);

    @NonNull
    @Mapping(source = "coordinateX", target = "coordinateX")
    @Mapping(source = "coordinateZ", target = "coordinateY")
    ForestItem toForestItem(final MapStructureEntity mapStructureEntity);

}