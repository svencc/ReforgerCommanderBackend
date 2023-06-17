package com.rcb.repository.configuration;

import com.rcb.entity.Configuration;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConfigurationPersistenceLayer {

    @NonNull
    private final ConfigurationRepository configurationRepository;

    @NonNull
    public List<Configuration> findValues(
            @NonNull final String mapName,
            @NonNull final String namespace,
            @NonNull final String name
    ) {
        return configurationRepository.findAllByMapNameInAndNamespaceAndName(Arrays.asList(null, mapName), namespace, name);
    }

    @NonNull
    public List<Configuration> findNamespace(
            @NonNull final String mapName,
            @NonNull final String namespace
    ) {
        return configurationRepository.findAllByMapNameInAndNamespace(Arrays.asList(null, mapName), namespace);
    }

    @NonNull
    public List<Configuration> findAllDefaultValueEntities() {
        return configurationRepository.findAllByMapNameIsNull();
    }

    @NonNull
    public List<Configuration> saveAll(@NonNull final List<Configuration> entities) {
        return configurationRepository.saveAll(entities);
    }

    public void deleteAll(@NonNull final List<Configuration> entities) {
        configurationRepository.deleteAll(entities);
    }

}
