package com.recom.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.recom.dto.map.scanner.topography.MapTopographyEntityDto;
import com.recom.entity.MapTopographyEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface MapTopographyEntityMapper {

    MapTopographyEntityMapper INSTANCE = Mappers.getMapper(MapTopographyEntityMapper.class);

    @Nullable
    @Named("encodeVectorToJsonString")
    static String encodeVectorToJsonString(@Nullable final List<BigDecimal> vectorXYZ) throws JsonProcessingException {
        return MapperUtil.encodeVectorToJsonString(vectorXYZ);
    }

    @Nullable
    @Named("decodeJsonStringToVector")
    static List<BigDecimal> decodeJsonStringToVector(@Nullable final String vectorXYZString) throws JsonProcessingException {
        return MapperUtil.decodeJsonStringToVector(vectorXYZString);
    }

    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "surfaceHeight", target = "surfaceHeight")
    @Mapping(source = "oceanHeight", target = "oceanHeight")
    @Mapping(source = "oceanBaseHeight", target = "oceanBaseHeight")
    @Mapping(source = "coordinates", target = "coordinates", qualifiedByName = "encodeVectorToJsonString")
    MapTopographyEntity toEntity(final MapTopographyEntityDto mapTopographyEntityDto);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "surfaceHeight", target = "surfaceHeight")
    @Mapping(source = "oceanHeight", target = "oceanHeight")
    @Mapping(source = "oceanBaseHeight", target = "oceanBaseHeight")
    @Mapping(source = "coordinates", target = "coordinates", qualifiedByName = "decodeJsonStringToVector")
    MapTopographyEntityDto toDto(final MapTopographyEntity entity);

}