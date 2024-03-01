package com.recom.mapper.mapcomposer;

import com.recom.commons.model.maprendererpipeline.MapLayerRasterizerConfiguration;
import com.recom.dto.map.mapcomposer.MapLayerRasterizerConfigurationDto;
import lombok.NonNull;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MapLayerRasterizerConfigurationMapper {

    MapLayerRasterizerConfigurationMapper INSTANCE = Mappers.getMapper(MapLayerRasterizerConfigurationMapper.class);

    @NonNull
    MapLayerRasterizerConfiguration toMapLayerRasterizerConfiguration(final MapLayerRasterizerConfigurationDto dto);

}