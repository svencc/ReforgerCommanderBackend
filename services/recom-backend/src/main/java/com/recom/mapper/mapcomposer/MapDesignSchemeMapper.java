package com.recom.mapper.mapcomposer;

import com.recom.commons.map.rasterizer.mapdesignscheme.MapDesignSchemeImplementation;
import com.recom.dto.map.mapcomposer.MapDesignSchemeDto;
import lombok.NonNull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MapDesignSchemeMapper {

    MapDesignSchemeMapper INSTANCE = Mappers.getMapper(MapDesignSchemeMapper.class);

    @NonNull
    MapDesignSchemeImplementation toDesignScheme(final MapDesignSchemeDto dto);

}