package com.recom.service.configuration;

import com.recom.dto.configuration.get.ConfigurationListDto;
import com.recom.dto.configuration.get.OverridableConfigurationDto;
import com.recom.dto.configuration.post.OverrideConfigurationDto;
import com.recom.dto.configuration.post.OverrideConfigurationListDto;
import com.recom.entity.Configuration;
import com.recom.mapper.ConfigurationMapper;
import com.recom.repository.configuration.ConfigurationPersistenceLayer;
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
    public ConfigurationListDto provideAllExistingConfigurationValues(@NonNull final String mapName) {
        return enhanceDefaultConfigurationWithMapSpecificOverrideValues(mapName, provideAllExistingConfigurationValues());
    }

    @NonNull
    private ConfigurationListDto enhanceDefaultConfigurationWithMapSpecificOverrideValues(
            @NonNull final String mapName,
            @NonNull final ConfigurationListDto defaultConfigurationListDto
    ) {
        final List<Configuration> mapSpecificConfigurationValues = configurationPersistenceLayer.findAllMapSpecificValueEntities(mapName);
        final Map<String, Map<String, List<Configuration>>> indexedConfigurationList = createIndexedConfigurationMap(mapSpecificConfigurationValues);

        defaultConfigurationListDto.setMapName(mapName);
        defaultConfigurationListDto.getConfigurationList()
                .forEach((final OverridableConfigurationDto defaultConfiguration) -> findConfigurationInIndexedMap(indexedConfigurationList, defaultConfiguration.getNamespace(), defaultConfiguration.getName()).ifPresent(
                        (mapSpecificConfiguration) -> {
                            defaultConfiguration.setMapOverriddenValue(mapSpecificConfiguration.getValue());
                        }
                ));

        return defaultConfigurationListDto;
    }

    @NonNull
    public ConfigurationListDto provideAllExistingConfigurationValues() {
        return ConfigurationListDto.builder()
                .configurationList(configurationValueProvider.provideAllExistingDefaultValues().stream()
                        .map(ConfigurationMapper.INSTANCE::toDto)
                        .collect(Collectors.toList()))
                .build();
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
    public void updateOverrides(@NonNull final OverrideConfigurationListDto overrideList) {
        // @TODO REFACTOR MOVE configurationPersistenceLayer.findAllMapSpecificValueEntities -> configurationValueProvider.provideAllExistingDefaultValues()
        final Map<String, Map<String, List<Configuration>>> indexedExistingOverrideConfigurationList = createIndexedConfigurationMap(configurationPersistenceLayer.findAllMapSpecificValueEntities(overrideList.getMapName()));
        final Map<String, Map<String, List<Configuration>>> indexedDefaultConfigurationList = createIndexedConfigurationMap(configurationValueProvider.provideAllExistingDefaultValues());

        final List<Configuration> configurationsToCreate = new ArrayList<>();
        final List<Configuration> configurationsToUpdate = new ArrayList<>();
        final List<Configuration> configurationsToDelete = new ArrayList<>();

        overrideList.getConfigurationList().forEach(override -> findConfigurationInIndexedMap(indexedDefaultConfigurationList, override.getNamespace(), override.getName())
                .ifPresentOrElse(
                        handleOverrideWithCorrespondingDefaultConfiguration(overrideList, indexedExistingOverrideConfigurationList, configurationsToCreate, configurationsToUpdate, override),
                        otherwiseTidyUpOldOverrideEntries(indexedExistingOverrideConfigurationList, configurationsToDelete, override)
                ));

        configurationPersistenceLayer.saveAll(configurationsToCreate);
        configurationPersistenceLayer.saveAll(configurationsToUpdate);
        configurationPersistenceLayer.deleteAll(configurationsToDelete);
    }

    @NonNull
    private Consumer<Configuration> handleOverrideWithCorrespondingDefaultConfiguration(
            @NonNull final OverrideConfigurationListDto overrideList,
            @NonNull final Map<String, Map<String, List<Configuration>>> indexedExistingOverrideConfigurationList,
            @NonNull final List<Configuration> configurationsToCreate,
            @NonNull final List<Configuration> configurationsToUpdate,
            @NonNull final OverrideConfigurationDto override
    ) {
        return (final Configuration existingDefaultConfiguration) -> {
            findConfigurationInIndexedMap(indexedExistingOverrideConfigurationList, override.getNamespace(), override.getName()).ifPresentOrElse(
                    (final Configuration existingOverride) -> {
                        existingOverride.setType(override.getType());
                        existingOverride.setValue(override.getMapOverrideValue());
                        configurationsToUpdate.add(existingOverride);
                    },
                    () -> {
                        final Configuration newConfigurationOverride = Configuration.builder()
                                .mapName(overrideList.getMapName())
                                .namespace(override.getNamespace())
                                .name(override.getName())
                                .type(override.getType())
                                .value(override.getMapOverrideValue())
                                .build();
                        configurationsToCreate.add(newConfigurationOverride);
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
