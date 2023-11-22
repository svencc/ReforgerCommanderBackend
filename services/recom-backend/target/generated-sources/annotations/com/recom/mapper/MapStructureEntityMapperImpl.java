package com.recom.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.recom.dto.map.scanner.structure.MapStructureEntityDto;
import com.recom.entity.MapStructureEntity;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-11-22T23:34:37+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21 (Azul Systems, Inc.)"
)
public class MapStructureEntityMapperImpl implements MapStructureEntityMapper {

    @Override
    public MapStructureEntity toEntity(MapStructureEntityDto mapStructureEntityDto) {
        if ( mapStructureEntityDto == null ) {
            return null;
        }

        MapStructureEntity.MapStructureEntityBuilder mapStructureEntity = MapStructureEntity.builder();

        mapStructureEntity.entityId( mapStructureEntityDto.getEntityId() );
        try {
            mapStructureEntity.name( MapStructureEntityMapper.blankStringToNull( mapStructureEntityDto.getName() ) );
        }
        catch ( JsonProcessingException e ) {
            throw new RuntimeException( e );
        }
        mapStructureEntity.className( mapStructureEntityDto.getClassName() );
        try {
            mapStructureEntity.prefabName( MapStructureEntityMapper.blankStringToNull( mapStructureEntityDto.getPrefabName() ) );
        }
        catch ( JsonProcessingException e ) {
            throw new RuntimeException( e );
        }
        try {
            mapStructureEntity.resourceName( MapStructureEntityMapper.blankStringToNull( mapStructureEntityDto.getResourceName() ) );
        }
        catch ( JsonProcessingException e ) {
            throw new RuntimeException( e );
        }
        try {
            mapStructureEntity.mapDescriptorType( MapStructureEntityMapper.blankStringToNull( mapStructureEntityDto.getMapDescriptorType() ) );
        }
        catch ( JsonProcessingException e ) {
            throw new RuntimeException( e );
        }
        try {
            mapStructureEntity.rotationX( MapStructureEntityMapper.encodeVectorToJsonString( mapStructureEntityDto.getRotationX() ) );
        }
        catch ( JsonProcessingException e ) {
            throw new RuntimeException( e );
        }
        try {
            mapStructureEntity.rotationY( MapStructureEntityMapper.encodeVectorToJsonString( mapStructureEntityDto.getRotationY() ) );
        }
        catch ( JsonProcessingException e ) {
            throw new RuntimeException( e );
        }
        try {
            mapStructureEntity.rotationZ( MapStructureEntityMapper.encodeVectorToJsonString( mapStructureEntityDto.getRotationZ() ) );
        }
        catch ( JsonProcessingException e ) {
            throw new RuntimeException( e );
        }
        mapStructureEntity.coordinateX( MapStructureEntityMapper.extractCoordinateX( mapStructureEntityDto.getCoordinates() ) );
        mapStructureEntity.coordinateY( MapStructureEntityMapper.extractCoordinateY( mapStructureEntityDto.getCoordinates() ) );
        mapStructureEntity.coordinateZ( MapStructureEntityMapper.extractCoordinateZ( mapStructureEntityDto.getCoordinates() ) );

        return mapStructureEntity.build();
    }

    @Override
    public MapStructureEntityDto toDto(MapStructureEntity entity) {
        if ( entity == null ) {
            return null;
        }

        MapStructureEntityDto.MapStructureEntityDtoBuilder mapStructureEntityDto = MapStructureEntityDto.builder();

        mapStructureEntityDto.entityId( entity.getEntityId() );
        mapStructureEntityDto.name( entity.getName() );
        mapStructureEntityDto.className( entity.getClassName() );
        mapStructureEntityDto.prefabName( entity.getPrefabName() );
        mapStructureEntityDto.resourceName( entity.getResourceName() );
        mapStructureEntityDto.mapDescriptorType( entity.getMapDescriptorType() );
        try {
            mapStructureEntityDto.rotationX( MapStructureEntityMapper.decodeJsonStringToVector( entity.getRotationX() ) );
        }
        catch ( JsonProcessingException e ) {
            throw new RuntimeException( e );
        }
        try {
            mapStructureEntityDto.rotationY( MapStructureEntityMapper.decodeJsonStringToVector( entity.getRotationY() ) );
        }
        catch ( JsonProcessingException e ) {
            throw new RuntimeException( e );
        }
        try {
            mapStructureEntityDto.rotationZ( MapStructureEntityMapper.decodeJsonStringToVector( entity.getRotationZ() ) );
        }
        catch ( JsonProcessingException e ) {
            throw new RuntimeException( e );
        }
        mapStructureEntityDto.coordinates( MapStructureEntityMapper.joinEntityCoordinatesToDtoCoordinates( entity ) );

        return mapStructureEntityDto.build();
    }
}
