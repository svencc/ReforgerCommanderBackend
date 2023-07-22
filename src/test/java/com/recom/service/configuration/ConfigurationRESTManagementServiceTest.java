package com.recom.service.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recom.dto.configuration.OverridableConfigurationDto;
import com.recom.dto.configuration.OverrideConfigurationDto;
import com.recom.entity.Configuration;
import com.recom.model.configuration.ConfigurationType;
import com.recom.repository.configuration.ConfigurationPersistenceLayer;
import com.recom.service.provider.StaticObjectMapperProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class ConfigurationRESTManagementServiceTest {

    @Mock
    private ConfigurationPersistenceLayer configurationPersistenceLayer;
    @Mock
    private ConfigurationValueProvider configurationValueProvider;
    @InjectMocks
    private ConfigurationRESTManagementService serviceUnderTest;

    @BeforeEach
    public void beforeEach() {
        // Arrange
        final StaticObjectMapperProvider staticObjectMapperProvider = new StaticObjectMapperProvider(new ObjectMapper());
        staticObjectMapperProvider.postConstruct();
    }


    @Test
    void testProvideAllExistingConfigurationValues() {
        // Arrange
        when(configurationValueProvider.provideAllExistingDefaultValues())
                .thenReturn(List.of(
                        Configuration.builder()
                                .mapName(null)
                                .namespace("namespace")
                                .name("value")
                                .type(ConfigurationType.INTEGER)
                                .value("1")
                                .build(),
                        Configuration.builder()
                                .mapName(null)
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

        // Map specific configuration values
        when(configurationPersistenceLayer.findAllMapSpecificValueEntities(eq(mapName)))
                .thenReturn(List.of(
                        Configuration.builder()
                                .mapName(mapName)
                                .namespace("namespace")
                                .name("value")
                                .type(ConfigurationType.INTEGER)
                                .value("2")
                                .build(),
                        Configuration.builder()
                                .mapName(mapName)
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
                                .mapName(null)
                                .namespace("namespace")
                                .name("value")
                                .type(ConfigurationType.INTEGER)
                                .value("1")
                                .build(),
                        Configuration.builder()
                                .mapName(null)
                                .namespace("namespace")
                                .name("list")
                                .type(ConfigurationType.LIST)
                                .value("[\"1\",\"2\"]")
                                .build()
                ));

        // Act
        final List<OverridableConfigurationDto> listToTest = serviceUnderTest.provideAllExistingConfigurationValues(mapName);

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

    @Disabled
    @Test
    void testUpdateOverrides_whenMapNameGive_shouldMergeWithOverriddenMapConfigurationData() {
        // Arrange
        final String mapName = "mapName";

        final List<OverrideConfigurationDto> overrideList = List.of(
                OverrideConfigurationDto.builder()
                        .namespace("namespace")
                        .name("value")
                        .build(),
                OverrideConfigurationDto.builder()
                        .namespace("namespace")
                        .name("list")
                        .build()
        );

        // Act
        serviceUnderTest.updateOverrides(mapName, overrideList);

        // Assert
        // test with argument captor
//        configurationPersistenceLayer.saveAll();
//        configurationPersistenceLayer.saveAll();
//        configurationPersistenceLayer.deleteAll();
    }

}