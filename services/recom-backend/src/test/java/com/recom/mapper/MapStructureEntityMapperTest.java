package com.recom.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recom.dto.map.scanner.structure.MapStructureDto;
import com.recom.entity.map.structure.*;
import com.recom.mapper.mapstructure.MapStructureEntityMapper;
import com.recom.service.provider.StaticObjectMapperProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MapStructureEntityMapperTest {

    ObjectMapper objectMapper;

    @BeforeEach
    public void beforeEach() {
        objectMapper = new ObjectMapper();
        final StaticObjectMapperProvider staticObjectMapperProvider = new StaticObjectMapperProvider(objectMapper);
        staticObjectMapperProvider.postConstruct();
    }

    @Test
    public void testToEntity() throws JsonProcessingException {
        // Arrange
        final String entityId = "0x60000000000019A9 {}";
        final String className = "GenericEntity";
        final String prefabName = "Prefabs/Editor/EditorServer.et";
        final String resourceName = "{98FD8652868F8C56}Assets/Rocks/Granite/GraniteCliff_03.xob";
        final String mapTypeDescriptor = "mapTypeDescriptor";
        final List<BigDecimal> rotationX = List.of(BigDecimal.valueOf(1.0), BigDecimal.ZERO, BigDecimal.ZERO);
        final List<BigDecimal> rotationY = List.of(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.valueOf(1.0));
        final List<BigDecimal> rotationZ = List.of(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.valueOf(1.0));
        final List<BigDecimal> coordinates = List.of(BigDecimal.valueOf(1.0), BigDecimal.valueOf(2.0), BigDecimal.valueOf(3.0));

        final MapStructureDto dto = MapStructureDto.builder()
                .entityId(entityId)
                .name(null)
                .className(className)
                .prefabName(prefabName)
                .resourceName(resourceName)
                .mapDescriptorType(mapTypeDescriptor)
                .rotationX(rotationX)
                .rotationY(rotationY)
                .rotationZ(rotationZ)
                .coordinates(coordinates)
                .build();

        // Act
        final MapStructureEntity entityToTest = MapStructureEntityMapper.INSTANCE.toEntity(dto);

        // Assert
        assertEquals(entityId, entityToTest.getEntityId());
        assertNull(entityToTest.getName());
        assertNull(entityToTest.getClassName());
        assertNull(entityToTest.getPrefabName());
        assertNull(entityToTest.getResourceName());
        assertNull(entityToTest.getMapDescriptorType());
        assertEquals(objectMapper.writeValueAsString(rotationX), entityToTest.getRotationX());
        assertEquals(objectMapper.writeValueAsString(rotationY), entityToTest.getRotationY());
        assertEquals(objectMapper.writeValueAsString(rotationZ), entityToTest.getRotationZ());
        assertEquals(BigDecimal.valueOf(1.0), entityToTest.getCoordinateX());
        assertEquals(BigDecimal.valueOf(2.0), entityToTest.getCoordinateY());
        assertEquals(BigDecimal.valueOf(3.0), entityToTest.getCoordinateZ());
    }

    @Test
    public void testToDto() throws JsonProcessingException {
        final String entityId = "0x60000000000019A9 {}";
        final String className = "GenericEntity";
        final String prefabName = "Prefabs/Editor/EditorServer.et";
        final String resourceName = "{98FD8652868F8C56}Assets/Rocks/Granite/GraniteCliff_03.xob";
        final String mapTypeDescriptor = "mapTypeDescriptor";
        final List<BigDecimal> rotationX = List.of(BigDecimal.valueOf(1.0), BigDecimal.ZERO, BigDecimal.ZERO);
        final List<BigDecimal> rotationY = List.of(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.valueOf(1.0));
        final List<BigDecimal> rotationZ = List.of(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.valueOf(1.0));
        final List<BigDecimal> coordinates = List.of(BigDecimal.valueOf(1.0), BigDecimal.valueOf(2.0), BigDecimal.valueOf(3.0));

        final MapStructureEntity entity = MapStructureEntity.builder()
                .entityId(entityId)
                .name(null)
                .className(ClassNameEntity.builder().name(className).build())
                .prefabName(PrefabNameEntity.builder().name(prefabName).build())
                .resourceName(ResourceNameEntity.builder().name(resourceName).build())
                .mapDescriptorType(MapDescriptorTypeEntity.builder().name(mapTypeDescriptor).build())
                .rotationX(objectMapper.writeValueAsString(rotationX))
                .rotationY(objectMapper.writeValueAsString(rotationY))
                .rotationZ(objectMapper.writeValueAsString(rotationZ))
                .coordinateX(BigDecimal.valueOf(1.0))
                .coordinateY(BigDecimal.valueOf(2.0))
                .coordinateZ(BigDecimal.valueOf(3.0))
                .build();

        // Act
        final MapStructureDto dtoToTest = MapStructureEntityMapper.INSTANCE.toDto(entity);

        // Assert
        assertEquals(entityId, dtoToTest.getEntityId());
        assertNull(dtoToTest.getName());
        assertEquals(className, dtoToTest.getClassName());
        assertEquals(prefabName, dtoToTest.getPrefabName());
        assertEquals(resourceName, dtoToTest.getResourceName());
        assertEquals(mapTypeDescriptor, dtoToTest.getMapDescriptorType());
        assertTrue(rotationX.containsAll(dtoToTest.getRotationX()));
        assertTrue(rotationY.containsAll(dtoToTest.getRotationY()));
        assertTrue(rotationZ.containsAll(dtoToTest.getRotationZ()));
        assertTrue(coordinates.containsAll(dtoToTest.getCoordinates()));
    }

}