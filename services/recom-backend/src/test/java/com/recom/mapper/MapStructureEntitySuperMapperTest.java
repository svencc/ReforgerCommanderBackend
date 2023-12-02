package com.recom.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recom.dto.map.scanner.structure.MapStructureEntityDto;
import com.recom.entity.map.structure.ClassNameEntity;
import com.recom.entity.map.structure.MapStructureEntity;
import com.recom.entity.map.structure.PrefabNameEntity;
import com.recom.mapper.mapstructure.MapStructureEntityMapper;
import com.recom.mapper.mapstructure.MapStructureEntitySuperMapper;
import com.recom.persistence.map.structure.MapStructurePersistenceLayer;
import com.recom.service.provider.StaticObjectMapperProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MapStructureEntitySuperMapperTest {

    @Mock
    private MapStructurePersistenceLayer mapStructurePersistenceLayer;
    @InjectMocks
    private MapStructureEntitySuperMapper mapStructureEntitySuperMapper;
    private ObjectMapper objectMapper;

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

        final MapStructureEntityDto dto = MapStructureEntityDto.builder()
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
        mapStructureEntitySuperMapper.init();
        final MapStructureEntity entityToTest = mapStructureEntitySuperMapper.toEntity(dto);

        // Assert
        assertEquals(entityId, entityToTest.getEntityId());
        assertNull(entityToTest.getName());
        assertNotNull(entityToTest.getClassName());
        assertEquals(className, entityToTest.getClassName().getName());

        assertNotNull(entityToTest.getPrefabName());
        assertEquals(prefabName, entityToTest.getPrefabName().getName());

        assertEquals(resourceName, entityToTest.getResourceName());
        assertEquals(mapTypeDescriptor, entityToTest.getMapDescriptorType());
        assertEquals(objectMapper.writeValueAsString(rotationX), entityToTest.getRotationX());
        assertEquals(objectMapper.writeValueAsString(rotationY), entityToTest.getRotationY());
        assertEquals(objectMapper.writeValueAsString(rotationZ), entityToTest.getRotationZ());
        assertEquals(BigDecimal.valueOf(1.0), entityToTest.getCoordinateX());
        assertEquals(BigDecimal.valueOf(2.0), entityToTest.getCoordinateY());
        assertEquals(BigDecimal.valueOf(3.0), entityToTest.getCoordinateZ());
    }

}