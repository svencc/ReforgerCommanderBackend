package com.recom.mapper;

import com.recom.dto.configuration.OverridableConfigurationDto;
import com.recom.entity.Configuration;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-11-22T23:34:19+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21 (Azul Systems, Inc.)"
)
public class ConfigurationMapperImpl implements ConfigurationMapper {

    @Override
    public OverridableConfigurationDto toDto(Configuration entity) {
        if ( entity == null ) {
            return null;
        }

        OverridableConfigurationDto.OverridableConfigurationDtoBuilder overridableConfigurationDto = OverridableConfigurationDto.builder();

        overridableConfigurationDto.defaultValue( entity.getValue() );
        overridableConfigurationDto.namespace( entity.getNamespace() );
        overridableConfigurationDto.name( entity.getName() );
        overridableConfigurationDto.type( entity.getType() );

        return overridableConfigurationDto.build();
    }
}
