package com.recom.mapper;

import com.recom.dto.map.topography.HeightMapDescriptorDto;
import com.recom.commons.model.DEMDescriptor;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface HeightMapDescriptorMapper {

    HeightMapDescriptorMapper INSTANCE = Mappers.getMapper(HeightMapDescriptorMapper.class);

    HeightMapDescriptorDto toDto(
            final DEMDescriptor command,
            final String mapName
    );

}