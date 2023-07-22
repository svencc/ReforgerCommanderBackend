package com.recom.mapper;

import com.recom.dto.configuration.OverridableConfigurationDto;
import com.recom.entity.Configuration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ConfigurationMapper {

    ConfigurationMapper INSTANCE = Mappers.getMapper(ConfigurationMapper.class);

    @Mapping(source = "value", target = "defaultValue")
    OverridableConfigurationDto toDto(final Configuration entity);

}