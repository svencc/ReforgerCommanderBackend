package com.recom.mapper.mapcomposer;

import com.recom.commons.model.maprendererpipeline.dataprovider.structure.StructureItem;
import com.recom.entity.map.structure.MapStructureEntity;
import lombok.NonNull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface VillageItemMapper {

    VillageItemMapper INSTANCE = Mappers.getMapper(VillageItemMapper.class);

    @NonNull
    @Mapping(source = "coordinateX", target = "coordinateX")
    @Mapping(source = "coordinateZ", target = "coordinateY")
    StructureItem toVillageItem(final MapStructureEntity mapStructureEntity);

}