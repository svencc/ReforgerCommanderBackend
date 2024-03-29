package com.recom.service.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.recom.dto.configuration.OverridableConfigurationDto;
import com.recom.dto.configuration.OverrideConfigurationDto;
import com.recom.entity.Configuration;
import com.recom.entity.map.GameMap;
import com.recom.mapper.ConfigurationMapper;
import com.recom.model.configuration.ConfigurationType;
import com.recom.persistence.configuration.ConfigurationPersistenceLayer;
import com.recom.service.provider.StaticObjectMapperProvider;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigurationRESTManagementService {

    @NonNull
    private final ConfigurationPersistenceLayer configurationPersistenceLayer;
    @NonNull
    private final ConfigurationValueProvider configurationValueProvider;


    @NonNull
    public List<OverridableConfigurationDto> provideAllExistingConfigurationValues(@NonNull final GameMap gameMap) {
        return enhanceDefaultConfigurationWithMapSpecificOverrideValues(gameMap, provideAllExistingConfigurationValues());
    }

    @NonNull
    private List<OverridableConfigurationDto> enhanceDefaultConfigurationWithMapSpecificOverrideValues(
            @NonNull final GameMap gameMap,
            @NonNull final List<OverridableConfigurationDto> defaultConfigurationListDto
    ) {
        final List<Configuration> mapSpecificConfigurationValues = configurationPersistenceLayer.findAllMapSpecificValueEntities(gameMap);
        final Map<String, Map<String, List<Configuration>>> indexedConfigurationList = createIndexedConfigurationMap(mapSpecificConfigurationValues);

        defaultConfigurationListDto.forEach((final OverridableConfigurationDto defaultConfiguration) -> findConfigurationInIndexedMap(indexedConfigurationList, defaultConfiguration.getNamespace(), defaultConfiguration.getName()).ifPresent(
                (mapSpecificConfiguration) -> {
                    defaultConfiguration.setMapOverriddenValue(mapSpecificConfiguration.getValue());
                }
        ));

        return defaultConfigurationListDto;
    }

    @NonNull
    public List<OverridableConfigurationDto> provideAllExistingConfigurationValues() {
        return configurationValueProvider.provideAllExistingDefaultValues().stream()
                .map(ConfigurationMapper.INSTANCE::toDto)
                .collect(Collectors.toList());
    }

    @NonNull
    private Map<String, Map<String, List<Configuration>>> createIndexedConfigurationMap(@NonNull final List<Configuration> configurationValuesToIndex) {
        return configurationValuesToIndex.stream()
                .collect(Collectors.groupingBy(Configuration::getNamespace, Collectors.groupingBy(Configuration::getName)));
    }

    @NonNull
    private Optional<Configuration> findConfigurationInIndexedMap(
            @NonNull final Map<String, Map<String, List<Configuration>>> preIndexedConfigurationList,
            @NonNull final String namespace,
            @NonNull final String name
    ) {
        return Optional.ofNullable(preIndexedConfigurationList.get(namespace))
                .map(map -> map.get(name))
                .flatMap(list -> list.stream().findFirst());
    }

    @Transactional(readOnly = false)
    public void updateOverrides(
            @NonNull final GameMap gameMap,
            @NonNull final List<OverrideConfigurationDto> overrideList
    ) {
        // @TODO REFACTOR MOVE configurationPersistenceLayer.findAllMapSpecificValueEntities -> configurationValueProvider.provideAllExistingDefaultValues()
        final Map<String, Map<String, List<Configuration>>> indexedExistingOverrideConfigurationList = createIndexedConfigurationMap(configurationPersistenceLayer.findAllMapSpecificValueEntities(gameMap));
        final Map<String, Map<String, List<Configuration>>> indexedDefaultConfigurationList = createIndexedConfigurationMap(configurationValueProvider.provideAllExistingDefaultValues());

        final List<Configuration> configurationsToCreate = new ArrayList<>();
        final List<Configuration> configurationsToUpdate = new ArrayList<>();
        final List<Configuration> configurationsToDelete = new ArrayList<>();

        overrideList.forEach(override -> findConfigurationInIndexedMap(indexedDefaultConfigurationList, override.getNamespace(), override.getName())
                .ifPresentOrElse(
                        handleOverrideWithCorrespondingDefaultConfiguration(gameMap, indexedExistingOverrideConfigurationList, configurationsToCreate, configurationsToUpdate, configurationsToDelete, override),
                        otherwiseTidyUpOldOverrideEntries(indexedExistingOverrideConfigurationList, configurationsToDelete, override)
                ));

        configurationPersistenceLayer.saveAll(configurationsToCreate);
        configurationPersistenceLayer.saveAll(configurationsToUpdate);
        configurationPersistenceLayer.deleteAll(configurationsToDelete);
    }

    @NonNull
    private Consumer<Configuration> handleOverrideWithCorrespondingDefaultConfiguration(
            @NonNull final GameMap gameMap,
            @NonNull final Map<String, Map<String, List<Configuration>>> indexedExistingOverrideConfigurationList,
            @NonNull final List<Configuration> configurationsToCreate,
            @NonNull final List<Configuration> configurationsToUpdate,
            @NonNull final List<Configuration> configurationsToDelete,
            @NonNull final OverrideConfigurationDto override
    ) {
        return (final Configuration existingDefaultConfiguration) -> {
            findConfigurationInIndexedMap(indexedExistingOverrideConfigurationList, override.getNamespace(), override.getName()).ifPresentOrElse(
                    (final Configuration existingOverride) -> {
                        if (override.getMapOverriddenValue() == null && override.getMapOverriddenListValue() == null) {
                            configurationsToDelete.add(existingOverride);
                            return;
                        }

                        existingOverride.setType(override.getType());
                        existingOverride.setValue(override.getMapOverriddenValue());

                        if (override.getType() == ConfigurationType.LIST) {
                            try {
                                existingOverride.setValue(StaticObjectMapperProvider.provide().writeValueAsString(override.getMapOverriddenListValue()));
                            } catch (final JsonProcessingException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        configurationsToUpdate.add(existingOverride);
                    },
                    () -> {
                        final Configuration.ConfigurationBuilder configurationBuilder = Configuration.builder()
                                .gameMap(gameMap)
                                .namespace(override.getNamespace())
                                .name(override.getName())
                                .type(override.getType())
                                .value(override.getMapOverriddenValue());

                        if (override.getType() == ConfigurationType.LIST) {
                            try {
                                configurationBuilder.value(StaticObjectMapperProvider.provide().writeValueAsString(override.getMapOverriddenListValue()));
                            } catch (final JsonProcessingException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        final Configuration configurationToUpdate = configurationBuilder.build();

                        // only create new com.recom.configuration if an override value is set!
                        if (configurationToUpdate.getValue() != null) {
                            configurationsToCreate.add(configurationToUpdate);
                        }
                    }
            );
        };
    }

    @NonNull
    private Runnable otherwiseTidyUpOldOverrideEntries(
            @NonNull final Map<String, Map<String, List<Configuration>>> indexedExistingOverrideConfigurationList,
            @NonNull final List<Configuration> configurationsToDelete,
            @NonNull final OverrideConfigurationDto override
    ) {
        return () -> findConfigurationInIndexedMap(indexedExistingOverrideConfigurationList, override.getNamespace(), override.getName())
                .ifPresent(configurationsToDelete::add);
    }

}
