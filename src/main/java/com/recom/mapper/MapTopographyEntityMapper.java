package com.recom.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.recom.dto.map.scanner.topography.MapTopographyEntityDto;
import com.recom.entity.MapEntity;
import com.recom.entity.MapTopographyEntity;
import com.recom.event.listener.generic.MapLocatedEntity;
import com.recom.event.listener.generic.TransactionalMapEntityMappable;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface MapTopographyEntityMapper extends TransactionalMapEntityMappable<MapTopographyEntity, MapTopographyEntityDto> {

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

    @Nullable
    @Named("extractCoordinateX")
    static BigDecimal extractCoordinateX(@Nullable final List<BigDecimal> coordinates) {
        return MapperUtil.extractCoordinateX(coordinates);
    }

    @Nullable
    @Named("extractCoordinateY")
    static BigDecimal extractCoordinateY(@Nullable final List<BigDecimal> coordinates) {
        return MapperUtil.extractCoordinateY(coordinates);
    }

    @Nullable
    @Named("extractCoordinateZ")
    static BigDecimal extractCoordinateZ(@Nullable final List<BigDecimal> coordinates) {
        return MapperUtil.extractCoordinateZ(coordinates);
    }

    @Nullable
    @Named("joinEntityCoordinatesToDtoCoordinates")
    static List<BigDecimal> joinEntityCoordinatesToDtoCoordinates(@Nullable final MapLocatedEntity entity) {
        return MapperUtil.joinEntityCoordinatesToDtoCoordinates(entity);
    }

    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "oceanHeight", target = "oceanHeight")
    @Mapping(source = "oceanBaseHeight", target = "oceanBaseHeight")
    @Mapping(source = "coordinates", target = "coordinateX", qualifiedByName = "extractCoordinateX")
    @Mapping(source = "coordinates", target = "coordinateY", qualifiedByName = "extractCoordinateY")
    @Mapping(source = "coordinates", target = "coordinateZ", qualifiedByName = "extractCoordinateZ")
    MapTopographyEntity toEntity(final MapTopographyEntityDto mapTopographyEntityDto);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "oceanHeight", target = "oceanHeight")
    @Mapping(source = "oceanBaseHeight", target = "oceanBaseHeight")
    @Mapping(source = "entity", target = "coordinates", qualifiedByName = "joinEntityCoordinatesToDtoCoordinates")
    MapTopographyEntityDto toDto(final MapTopographyEntity entity);

}