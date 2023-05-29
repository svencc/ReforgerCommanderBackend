package com.rcb.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rcb.dto.map.scanner.EntityDto;
import com.rcb.entity.MapEntity;
import com.rcb.service.provider.MapStructStaticProvider;
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
            return MapStructStaticProvider.provide().writeValueAsString(vectorXYZ);
        }
    }

    @NonNull
    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "entityId", target = "entityId")
    @Mapping(source = "className", target = "className")
    @Mapping(source = "resourceName", target = "resourceName")
    @Mapping(source = "rotationX", target = "rotationX", qualifiedByName = "encodeVectorToJsonString")
    @Mapping(source = "rotationY", target = "rotationY", qualifiedByName = "encodeVectorToJsonString")
    @Mapping(source = "rotationZ", target = "rotationZ", qualifiedByName = "encodeVectorToJsonString")
    @Mapping(source = "coords", target = "coords", qualifiedByName = "encodeVectorToJsonString")
    MapEntity toEntity(final EntityDto entityDto);

}