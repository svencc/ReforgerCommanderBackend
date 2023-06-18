package com.recom.service.configuration;

import com.recom.dto.configuration.get.OverridableConfigurationDto;
import com.recom.dto.configuration.get.ConfigurationListDto;
import com.recom.entity.Configuration;
import com.recom.mapper.ConfigurationMapper;
import com.recom.repository.configuration.ConfigurationPersistenceLayer;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    public ConfigurationListDto provideAllExistingDefaultConfigurationValues(@NonNull final String mapName) {
        return enhanceDefaultConfigurationWithMapSpecificConfigurationValues(mapName, provideAllExistingDefaultConfigurationValues());
    }

    @NonNull
    private ConfigurationListDto enhanceDefaultConfigurationWithMapSpecificConfigurationValues(String mapName, ConfigurationListDto defaultConfigurationListDto) {
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
    public ConfigurationListDto provideAllExistingDefaultConfigurationValues() {
        return ConfigurationListDto.builder()
                .configurationList(configurationValueProvider.provideAllExistingDefaultValueEntities().stream()
                        .map(ConfigurationMapper.INSTANCE::toDto)
                        .collect(Collectors.toList()))
                .build();
    }

    @NonNull
    private Map<String, Map<String, List<Configuration>>> createIndexedConfigurationMap(@NonNull final List<Configuration> mapSpecificConfigurationValues) {
        return mapSpecificConfigurationValues.stream()
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

}
