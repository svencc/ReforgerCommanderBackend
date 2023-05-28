package com.rcb.mapper;

import com.rcb.dto.mapScanner.MapScannerEntityDto;
import com.rcb.entity.MapEntity;
import jakarta.annotation.Nullable;
import lombok.NonNull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MapEntityMapper {
    MapEntityMapper INSTANCE = Mappers.getMapper(MapEntityMapper.class);

    @NonNull
    // @BeanMapping(ignoreByDefault = true)
//    @Mapping(source = "masters", target = "masters", qualifiedByName = "CategoryToString")
    MapEntity toDto(@NonNull MapScannerEntityDto entityDto);

    @Named("CategoryToString")
    private String defaultValueForQualifier(@Nullable String x) {
        return "x";
    }

    @NonNull
    private MapScannerEntityDto toEntity(@NonNull MapEntity entityDto) {
        return MapScannerEntityDto.builder()
                .build();
    }

}