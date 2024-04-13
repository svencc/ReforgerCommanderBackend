package com.recom.persistence.configuration;

import com.recom.entity.Configuration;
import com.recom.entity.map.GameMap;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.cache.annotation.CacheKey;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConfigurationPersistenceLayer {

    @NonNull
    private final ConfigurationRepository configurationRepository;


    @NonNull
    public List<Configuration> findValues(
            @CacheKey @NonNull final GameMap gameMap,
            @CacheKey @NonNull final String namespace,
            @CacheKey @NonNull final String name
    ) {
        return configurationRepository.findAllByGameMapAndNamespaceAndName(gameMap, namespace, name);
    }

    @NonNull
    public List<Configuration> findAllDefaultValueEntities() {
        return configurationRepository.findAllByGameMapIsNull();
    }

    @NonNull
    public List<Configuration> findAllMapSpecificValueEntities(@NonNull final GameMap gameMap) {
        return configurationRepository.findAllByGameMap(gameMap);
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
