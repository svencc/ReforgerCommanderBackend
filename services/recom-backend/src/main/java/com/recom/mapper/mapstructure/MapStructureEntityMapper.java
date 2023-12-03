package com.recom.mapper.mapstructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.recom.dto.map.scanner.structure.MapStructureEntityDto;
import com.recom.entity.map.structure.MapStructureEntity;
import com.recom.event.listener.generic.maplocated.MapLocatedEntity;
import com.recom.mapper.MapperUtil;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface MapStructureEntityMapper {

    MapStructureEntityMapper INSTANCE = Mappers.getMapper(MapStructureEntityMapper.class);

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
    @Named("blankStringToNull")
    static String blankStringToNull(@Nullable final String blankableString) throws JsonProcessingException {
        return MapperUtil.blankStringToNull(blankableString);
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
    @Mapping(source = "entityId", target = "entityId")
    @Mapping(source = "name", target = "name", qualifiedByName = "blankStringToNull")
//    @Mapping(source = "className", target = "className", qualifiedByName = "classNameStringToClassNameEntity")
//    @Mapping(source = "prefabName", target = "prefabName", qualifiedByName = "blankStringToNull")
//    @Mapping(source = "resourceName", target = "resourceName", qualifiedByName = "blankStringToNull")
//    @Mapping(source = "mapDescriptorType", target = "mapDescriptorType", qualifiedByName = "blankStringToNull")
    @Mapping(source = "rotationX", target = "rotationX", qualifiedByName = "encodeVectorToJsonString")
    @Mapping(source = "rotationY", target = "rotationY", qualifiedByName = "encodeVectorToJsonString")
    @Mapping(source = "rotationZ", target = "rotationZ", qualifiedByName = "encodeVectorToJsonString")
    @Mapping(source = "coordinates", target = "coordinateX", qualifiedByName = "extractCoordinateX")
    @Mapping(source = "coordinates", target = "coordinateY", qualifiedByName = "extractCoordinateY")
    @Mapping(source = "coordinates", target = "coordinateZ", qualifiedByName = "extractCoordinateZ")
    MapStructureEntity toEntity(final MapStructureEntityDto mapStructureEntityDto);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "entityId", target = "entityId")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "className.name", target = "className")
    @Mapping(source = "prefabName.name", target = "prefabName")
    @Mapping(source = "resourceName.name", target = "resourceName")
    @Mapping(source = "mapDescriptorType.name", target = "mapDescriptorType")
    @Mapping(source = "rotationX", target = "rotationX", qualifiedByName = "decodeJsonStringToVector")
    @Mapping(source = "rotationY", target = "rotationY", qualifiedByName = "decodeJsonStringToVector")
    @Mapping(source = "rotationZ", target = "rotationZ", qualifiedByName = "decodeJsonStringToVector")
    @Mapping(source = "entity", target = "coordinates", qualifiedByName = "joinEntityCoordinatesToDtoCoordinates")
    MapStructureEntityDto toDto(final MapStructureEntity entity);

}