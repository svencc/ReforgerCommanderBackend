package com.rcb.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rcb.dto.map.scanner.EntityDto;
import com.rcb.entity.MapEntity;
import com.rcb.service.provider.StaticObjectMapperProvider;
import lombok.NonNull;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface MapEntityMapper {

    MapEntityMapper INSTANCE = Mappers.getMapper(MapEntityMapper.class);

    @Nullable
    @Named("encodeVectorToJsonString")
    static String encodeVectorToJsonString(@Nullable final List<BigDecimal> vectorXYZ) throws JsonProcessingException {
        if (vectorXYZ == null) {
            return null;
        } else {
            return StaticObjectMapperProvider.provide().writeValueAsString(vectorXYZ);
        }
    }

    @Nullable
    @Named("blankStringToNull")
    static String blankStringToNull(@Nullable final String blankableString) throws JsonProcessingException {
        if (blankableString == null) {
            return null;
        } else {
            if (blankableString.isBlank()) {
                return null;
            } else {
                return blankableString;
            }
        }
    }

    @NonNull
    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "entityId", target = "entityId")
    @Mapping(source = "className", target = "className")
    @Mapping(source = "resourceName", target = "resourceName", qualifiedByName = "blankStringToNull")
    @Mapping(source = "rotationX", target = "rotationX", qualifiedByName = "encodeVectorToJsonString")
    @Mapping(source = "rotationY", target = "rotationY", qualifiedByName = "encodeVectorToJsonString")
    @Mapping(source = "rotationZ", target = "rotationZ", qualifiedByName = "encodeVectorToJsonString")
    @Mapping(source = "coordinates", target = "coordinates", qualifiedByName = "encodeVectorToJsonString")
    MapEntity toEntity(final EntityDto entityDto);

}