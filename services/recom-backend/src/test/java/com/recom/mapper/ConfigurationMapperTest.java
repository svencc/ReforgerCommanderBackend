package com.recom.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recom.dto.configuration.OverridableConfigurationDto;
import com.recom.entity.Configuration;
import com.recom.entity.map.GameMap;
import com.recom.model.configuration.ConfigurationType;
import com.recom.service.provider.StaticObjectMapperProvider;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConfigurationMapperTest {

    @Test
    public void testToDto() {
        // Arrange
        final GameMap gameMap = GameMap.builder().name("mapName").build();
        final Configuration configuration = Configuration.builder()
                .gameMap(gameMap)
                .namespace("namespace")
                .name("name")
                .value("value")
                .type(ConfigurationType.STRING)
                .build();

        // Act
        final OverridableConfigurationDto dtoToTest = ConfigurationMapper.INSTANCE.toDto(configuration);

        // Assert
        assertEquals(configuration.getNamespace(), dtoToTest.getNamespace());
        assertEquals(configuration.getName(), dtoToTest.getName());
        assertEquals(configuration.getType(), dtoToTest.getType());
        assertEquals(configuration.getValue(), dtoToTest.getDefaultValue());
    }

    @Test
    public void testToDto_whenTypeList_shouldUseListValueField() throws JsonProcessingException {
        // Arrange
        final ObjectMapper objectMapper = new ObjectMapper();
        StaticObjectMapperProvider staticObjectMapperProvider = new StaticObjectMapperProvider(objectMapper);
        staticObjectMapperProvider.postConstruct();

        final GameMap gameMap = GameMap.builder().name("mapName").build();
        final Configuration configuration = Configuration.builder()
                .gameMap(gameMap)
                .namespace("namespace")
                .name("name")
                .value(objectMapper.writeValueAsString(List.of("asd", "fgh")))
                .type(ConfigurationType.LIST)
                .build();

        // Act
        final OverridableConfigurationDto dtoToTest = ConfigurationMapper.INSTANCE.toDto(configuration);

        // Assert
        assertEquals(configuration.getNamespace(), dtoToTest.getNamespace());
        assertEquals(configuration.getName(), dtoToTest.getName());
        assertEquals(configuration.getType(), dtoToTest.getType());
        assertEquals(null, dtoToTest.getDefaultValue());
        assertNotNull(dtoToTest.getDefaultListValue());
        assertTrue(List.of("asd", "fgh").containsAll(dtoToTest.getDefaultListValue()));
    }

}