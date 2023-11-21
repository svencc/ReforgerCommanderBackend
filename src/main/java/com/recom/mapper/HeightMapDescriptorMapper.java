package com.recom.mapper;

import com.recom.dto.map.topography.MapTopographyDataResponseDto;
import com.recom.model.CreateHeightMapCommand;
import lombok.NonNull;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface HeightMapDescriptorMapper {

    HeightMapDescriptorMapper INSTANCE = Mappers.getMapper(HeightMapDescriptorMapper.class);

    //    @Mapping(source = "timestamp", target = "timestampEpochMilliseconds", qualifiedByName = "timestampToTimestampEpochMilliseconds")
    MapTopographyDataResponseDto toDto(
            @NonNull final CreateHeightMapCommand command,
            @NonNull final String mapName
    );

}