package com.recom.mapper.mapcomposer;

import com.recom.commons.map.rasterizer.mapdesignscheme.MapDesignScheme;
import com.recom.commons.model.maprendererpipeline.MapComposerConfiguration;
import com.recom.dto.map.mapcomposer.MapComposerConfigurationDto;
import com.recom.dto.map.mapcomposer.MapDesignSchemeDto;
import lombok.NonNull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MapComposerConfigurationMapper {

    MapComposerConfigurationMapper INSTANCE = Mappers.getMapper(MapComposerConfigurationMapper.class);

    @NonNull
    @Mapping(source = "mapDesignScheme", target = "mapDesignScheme", qualifiedByName = "mapDesignSchemeDtoToMapDesignScheme")
    MapComposerConfiguration toConfiguration(final MapComposerConfigurationDto dto);

    @Named("mapDesignSchemeDtoToMapDesignScheme")
    static MapDesignScheme mapDesignSchemeDtoToMapDesignScheme(MapDesignSchemeDto dto) {
        return MapDesignSchemeMapper.INSTANCE.toDesignScheme(dto);
    }

}