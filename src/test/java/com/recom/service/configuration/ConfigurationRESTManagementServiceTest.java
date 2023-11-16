package com.recom.service.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recom.dto.configuration.OverridableConfigurationDto;
import com.recom.dto.configuration.OverrideConfigurationDto;
import com.recom.entity.Configuration;
import com.recom.entity.GameMap;
import com.recom.model.configuration.ConfigurationType;
import com.recom.persistence.configuration.ConfigurationPersistenceLayer;
import com.recom.service.provider.StaticObjectMapperProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class ConfigurationRESTManagementServiceTest {

    @Mock
    private ConfigurationPersistenceLayer configurationPersistenceLayer;
    @Mock
    private ConfigurationValueProvider configurationValueProvider;
    @InjectMocks
    private ConfigurationRESTManagementService serviceUnderTest;
    @Captor
    private ArgumentCaptor<List<Configuration>> saveAllEntitiesCaptor;
    @Captor
    private ArgumentCaptor<List<Configuration>> deleteAllEntitiesCaptor;

    @BeforeEach
    public void beforeEach() {
        // Arrange
        final StaticObjectMapperProvider staticObjectMapperProvider = new StaticObjectMapperProvider(new ObjectMapper());
        staticObjectMapperProvider.postConstruct();
    }

    @Test
    void testProvideAllExistingConfigurationValues() {
        // Arrange
        final GameMap gameMap = GameMap.builder().name("mapName").build();
        when(configurationValueProvider.provideAllExistingDefaultValues())
                .thenReturn(List.of(
                        Configuration.builder()
                                .gameMap(gameMap)
                                .namespace("namespace")
                                .name("value")
                                .type(ConfigurationType.INTEGER)
                                .value("1")
                                .build(),
                        Configuration.builder()
                                .gameMap(gameMap)
                                .namespace("namespace")
                                .name("list")
                                .type(ConfigurationType.LIST)
                                .value("[\"1\",\"2\"]")
                                .build()
                ));

        // Act
        final List<OverridableConfigurationDto> listToTest = serviceUnderTest.provideAllExistingConfigurationValues();

        // Assert
        assertNotNull(listToTest);
        assertEquals(2, listToTest.size());
        assertEquals("namespace", listToTest.get(0).getNamespace());
        assertEquals("value", listToTest.get(0).getName());
        assertEquals("1", listToTest.get(0).getDefaultValue());
        assertNull(listToTest.get(0).getDefaultListValue());

        assertEquals("namespace", listToTest.get(1).getNamespace());
        assertEquals("list", listToTest.get(1).getName());
        assertNull(listToTest.get(1).getDefaultValue());
        assertNotNull(listToTest.get(1).getDefaultListValue());
        assertTrue(listToTest.get(1).getDefaultListValue().containsAll(List.of("1", "2")));
    }

    @Test
    void testProvideAllExistingConfigurationValues_whenMapNameGive_shouldMergeWithOverriddenMapConfigurationData() {
        // Arrange
        final String mapName = "mapName";
        final GameMap gameMap = GameMap.builder().name(mapName).build();

        // Map specific configuration values
        when(configurationPersistenceLayer.findAllMapSpecificValueEntities(eq(gameMap)))
                .thenReturn(List.of(
                        Configuration.builder()
                                .gameMap(gameMap)
                                .namespace("namespace")
                                .name("value")
                                .type(ConfigurationType.INTEGER)
                                .value("2")
                                .build(),
                        Configuration.builder()
                                .gameMap(gameMap)
                                .namespace("namespace")
                                .name("list")
                                .type(ConfigurationType.LIST)
                                .value("[\"1\",\"2\",\"3\"]")
                                .build()
                ));

        // Default configuration values
        when(configurationValueProvider.provideAllExistingDefaultValues())
                .thenReturn(List.of(
                        Configuration.builder()
                                .gameMap(null)
                                .namespace("namespace")
                                .name("value")
                                .type(ConfigurationType.INTEGER)
                                .value("1")
                                .build(),
                        Configuration.builder()
                                .gameMap(null)
                                .namespace("namespace")
                                .name("list")
                                .type(ConfigurationType.LIST)
                                .value("[\"1\",\"2\"]")
                                .build()
                ));

        // Act
        final List<OverridableConfigurationDto> listToTest = serviceUnderTest.provideAllExistingConfigurationValues(gameMap);

        // Assert
        assertNotNull(listToTest);
        assertEquals(2, listToTest.size());
        assertEquals("namespace", listToTest.get(0).getNamespace());
        assertEquals("value", listToTest.get(0).getName());
        assertEquals("1", listToTest.get(0).getDefaultValue());
        assertNull(listToTest.get(0).getDefaultListValue());
        assertEquals("2", listToTest.get(0).getMapOverriddenValue());

        assertEquals("namespace", listToTest.get(1).getNamespace());
        assertEquals("list", listToTest.get(1).getName());
        assertNull(listToTest.get(1).getDefaultValue());
        assertTrue(listToTest.get(1).getDefaultListValue().containsAll(List.of("1", "2")));
        assertNull(listToTest.get(1).getMapOverriddenValue());
        assertTrue(listToTest.get(1).getMapOverriddenListValue().containsAll(List.of("1", "2", "3")));
    }

    @Test
    void testUpdateOverrides_whenMapNameGive_shouldMergeWithOverriddenMapConfigurationData() {
        // Arrange
        final String mapName = "mapName";
        final GameMap gameMap = GameMap.builder().name(mapName).build();
        final String namespace = "namespace";
        final List<OverrideConfigurationDto> overrideList = List.of(
                OverrideConfigurationDto.builder()
                        .namespace(namespace)
                        .name("value")
                        .type(ConfigurationType.INTEGER)
                        .mapOverriddenValue("2")
                        .build(),
                OverrideConfigurationDto.builder()
                        .namespace(namespace)
                        .name("list")
                        .type(ConfigurationType.LIST)
                        .mapOverriddenListValue(List.of("1", "2", "3"))
                        .build()
        );

        when(configurationValueProvider.provideAllExistingDefaultValues()).thenReturn(List.of(
                Configuration.builder()
                        .gameMap(null)
                        .namespace(namespace)
                        .name("value")
                        .type(ConfigurationType.INTEGER)
                        .value("1")
                        .build(),
                Configuration.builder()
                        .gameMap(null)
                        .namespace(namespace)
                        .name("list")
                        .type(ConfigurationType.LIST)
                        .value("[\"1\",\"2\"]")
                        .build()
        ));

        // Act
        serviceUnderTest.updateOverrides(gameMap, overrideList);

        // Assert
        // test that new and updated entities are saved ...
        verify(configurationPersistenceLayer, times(2)).saveAll(saveAllEntitiesCaptor.capture());
        // ... and deleted entities are deleted
        verify(configurationPersistenceLayer, times(1)).deleteAll(deleteAllEntitiesCaptor.capture());

        final List<List<Configuration>> saveAllCalls = saveAllEntitiesCaptor.getAllValues();
        final List<Configuration> deleteAllParameter = deleteAllEntitiesCaptor.getValue();

        // assert .saveAll() call and arguments
        assertEquals(2, saveAllCalls.size());
        assertEquals(2, saveAllCalls.get(0).size());
        assertEquals(0, saveAllCalls.get(1).size());

        final Configuration configuration1 = saveAllCalls.get(0).get(0);
        assertNull(configuration1.getId());
        assertEquals(mapName, configuration1.getMapName());
        assertEquals(namespace, configuration1.getNamespace());
        assertEquals(ConfigurationType.INTEGER, configuration1.getType());
        assertEquals("value", configuration1.getName());
        assertEquals("2", configuration1.getValue());

        Configuration configuration2 = saveAllCalls.get(0).get(1);
        assertNull(configuration2.getId());
        assertEquals(mapName, configuration2.getMapName());
        assertEquals(namespace, configuration2.getNamespace());
        assertEquals(ConfigurationType.LIST, configuration2.getType());
        assertEquals("list", configuration2.getName());
        assertEquals("[\"1\",\"2\",\"3\"]", configuration2.getValue());

        // .deleteAll()
        assertEquals(0, deleteAllParameter.size());
    }

}