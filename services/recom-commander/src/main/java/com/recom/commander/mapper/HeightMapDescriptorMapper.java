package com.recom.commander.mapper;

import com.recom.commons.model.DEMDescriptor;
import com.recom.dto.map.topography.HeightMapDescriptorDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface HeightMapDescriptorMapper {

    HeightMapDescriptorMapper INSTANCE = Mappers.getMapper(HeightMapDescriptorMapper.class);

    DEMDescriptor toModel(final HeightMapDescriptorDto command);

}