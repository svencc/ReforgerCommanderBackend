package com.recom.persistence.configuration;

import com.recom.entity.Configuration;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConfigurationPersistenceLayer {

    @NonNull
    private final ConfigurationRepository configurationRepository;

    @NonNull
    @Cacheable(cacheNames = "ConfigurationPersistenceLayer.findValues")
    public List<Configuration> findValues(
            @NonNull final String mapName,
            @NonNull final String namespace,
            @NonNull final String name
    ) {
        return configurationRepository.findAllByMapNameAndNamespaceAndName(mapName, namespace, name);
    }

    @NonNull
    @Cacheable(cacheNames = "ConfigurationPersistenceLayer.findAllDefaultValueEntities")
    public List<Configuration> findAllDefaultValueEntities() {
        return configurationRepository.findAllByMapNameIsNull();
    }

    @NonNull
    @Cacheable(cacheNames = "ConfigurationPersistenceLayer.findAllMapSpecificValueEntities")
    public List<Configuration> findAllMapSpecificValueEntities(@NonNull final String mapName) {
        return configurationRepository.findAllByMapName(mapName);
    }

    @NonNull
    public List<Configuration> saveAll(@NonNull final List<Configuration> entities) {
        return configurationRepository.saveAll(entities);
    }

    @NonNull
    public Configuration save(@NonNull final Configuration entity) {
        return configurationRepository.save(entity);
    }

    public void deleteAll(@NonNull final List<Configuration> entities) {
        configurationRepository.deleteAll(entities);
    }

}