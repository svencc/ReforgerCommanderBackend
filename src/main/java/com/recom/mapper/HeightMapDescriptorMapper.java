package com.recom.mapper;

import com.recom.dto.map.topography.MapTopographyDataResponseDto;
import com.recom.model.HeightMapDescriptor;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface HeightMapDescriptorMapper {

    HeightMapDescriptorMapper INSTANCE = Mappers.getMapper(HeightMapDescriptorMapper.class);

    MapTopographyDataResponseDto toDto(
            final HeightMapDescriptor command,
            final String mapName
    );

}