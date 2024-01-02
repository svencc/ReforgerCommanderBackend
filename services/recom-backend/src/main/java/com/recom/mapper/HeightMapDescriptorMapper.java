package com.recom.mapper;

import com.recom.dto.map.topography.HeightMapDescriptorDto;
import com.recom.rendertools.rasterizer.HeightMapDescriptor;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface HeightMapDescriptorMapper {

    HeightMapDescriptorMapper INSTANCE = Mappers.getMapper(HeightMapDescriptorMapper.class);

    HeightMapDescriptorDto toDto(
            final HeightMapDescriptor command,
            final String mapName
    );

}