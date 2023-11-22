package com.recom.mapper;

import com.recom.dto.map.topography.HeightMapDescriptorDto;
import com.recom.model.HeightMapDescriptor;
import java.util.Arrays;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-11-22T23:34:37+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21 (Azul Systems, Inc.)"
)
public class HeightMapDescriptorMapperImpl implements HeightMapDescriptorMapper {

    @Override
    public HeightMapDescriptorDto toDto(HeightMapDescriptor command, String mapName) {
        if ( command == null && mapName == null ) {
            return null;
        }

        HeightMapDescriptorDto.HeightMapDescriptorDtoBuilder heightMapDescriptorDto = HeightMapDescriptorDto.builder();

        if ( command != null ) {
            heightMapDescriptorDto.stepSize( command.getStepSize() );
            heightMapDescriptorDto.scanIterationsX( command.getScanIterationsX() );
            heightMapDescriptorDto.scanIterationsZ( command.getScanIterationsZ() );
            heightMapDescriptorDto.seaLevel( command.getSeaLevel() );
            heightMapDescriptorDto.maxWaterDepth( command.getMaxWaterDepth() );
            heightMapDescriptorDto.maxHeight( command.getMaxHeight() );
            float[][] heightMap = command.getHeightMap();
            if ( heightMap != null ) {
                heightMapDescriptorDto.heightMap( Arrays.copyOf( heightMap, heightMap.length ) );
            }
        }
        heightMapDescriptorDto.mapName( mapName );

        return heightMapDescriptorDto.build();
    }
}
