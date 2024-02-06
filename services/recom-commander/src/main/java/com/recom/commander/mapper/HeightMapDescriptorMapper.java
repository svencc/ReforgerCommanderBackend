package com.recom.commander.mapper;

import com.recom.dto.map.topography.HeightMapDescriptorDto;
import com.recom.commons.rasterizer.HeightMapDescriptor;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface HeightMapDescriptorMapper {

    HeightMapDescriptorMapper INSTANCE = Mappers.getMapper(HeightMapDescriptorMapper.class);

    HeightMapDescriptor toModel(final HeightMapDescriptorDto command);

}