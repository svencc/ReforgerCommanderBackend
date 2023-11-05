package com.recom.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.recom.dto.map.scanner.map.MapEntityDto;
import com.recom.entity.MapEntity;
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
public interface MapEntityMapper extends TransactionalMapEntityMappable<MapEntity, MapEntityDto> {

    MapEntityMapper INSTANCE = Mappers.getMapper(MapEntityMapper.class);

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

    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "entityId", target = "entityId")
    @Mapping(source = "name", target = "name", qualifiedByName = "blankStringToNull")
    @Mapping(source = "className", target = "className")
    @Mapping(source = "prefabName", target = "prefabName", qualifiedByName = "blankStringToNull")
    @Mapping(source = "resourceName", target = "resourceName", qualifiedByName = "blankStringToNull")
    @Mapping(source = "mapDescriptorType", target = "mapDescriptorType", qualifiedByName = "blankStringToNull")
    @Mapping(source = "rotationX", target = "rotationX", qualifiedByName = "encodeVectorToJsonString")
    @Mapping(source = "rotationY", target = "rotationY", qualifiedByName = "encodeVectorToJsonString")
    @Mapping(source = "rotationZ", target = "rotationZ", qualifiedByName = "encodeVectorToJsonString")
    @Mapping(source = "coordinates", target = "coordinates", qualifiedByName = "encodeVectorToJsonString")
    MapEntity toEntity(final MapEntityDto mapEntityDto);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "entityId", target = "entityId")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "className", target = "className")
    @Mapping(source = "prefabName", target = "prefabName")
    @Mapping(source = "resourceName", target = "resourceName")
    @Mapping(source = "mapDescriptorType", target = "mapDescriptorType")
    @Mapping(source = "rotationX", target = "rotationX", qualifiedByName = "decodeJsonStringToVector")
    @Mapping(source = "rotationY", target = "rotationY", qualifiedByName = "decodeJsonStringToVector")
    @Mapping(source = "rotationZ", target = "rotationZ", qualifiedByName = "decodeJsonStringToVector")
    @Mapping(source = "coordinates", target = "coordinates", qualifiedByName = "decodeJsonStringToVector")
    MapEntityDto toDto(final MapEntity entity);

}