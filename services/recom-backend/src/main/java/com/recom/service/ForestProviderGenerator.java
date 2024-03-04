package com.recom.service;

import com.recom.commons.model.maprendererpipeline.dataprovider.forest.ForestProvidable;
import com.recom.entity.map.GameMap;
import com.recom.mapper.mapcomposer.ForestItemMapper;
import com.recom.persistence.map.structure.MapStructurePersistenceLayer;
import com.recom.service.configuration.ConfigurationDescriptorProvider;
import com.recom.service.configuration.ConfigurationValueProvider;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ForestProviderGenerator {

    @NonNull
    private final MapStructurePersistenceLayer mapStructurePersistenceLayer;
    @NonNull
    private final ConfigurationValueProvider configurationValueProvider;


    @NonNull
    public ForestProvidable generate(@NonNull final GameMap gameMap) {
        return () -> {
            final List<String> forestResources = configurationValueProvider.queryValue(gameMap, ConfigurationDescriptorProvider.CLUSTERING_FOREST_RESOURCES_LIST);

            return mapStructurePersistenceLayer.findAllByMapNameAndResourceNameIn(gameMap, forestResources).stream()
                    .map(ForestItemMapper.INSTANCE::toForestItem)
                    .toList();
        };
    }

}
