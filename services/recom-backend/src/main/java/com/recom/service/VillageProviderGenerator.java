package com.recom.service;

import com.recom.commons.model.maprendererpipeline.dataprovider.village.VillageProvidable;
import com.recom.entity.map.GameMap;
import com.recom.mapper.mapcomposer.VillageItemMapper;
import com.recom.persistence.map.structure.MapStructurePersistenceLayer;
import com.recom.service.configuration.ConfigurationDescriptorProvider;
import com.recom.service.configuration.ConfigurationValueProvider;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VillageProviderGenerator {

    @NonNull
    private final MapStructurePersistenceLayer mapStructurePersistenceLayer;
    @NonNull
    private final ConfigurationValueProvider configurationValueProvider;


    @NonNull
    public VillageProvidable generate(@NonNull final GameMap gameMap) {
        return () -> {
            final List<String> villageResources = configurationValueProvider.queryValue(gameMap, ConfigurationDescriptorProvider.CLUSTERING_VILLAGE_RESOURCES_LIST);

            return mapStructurePersistenceLayer.findAllByMapNameAndResourceNameIn(gameMap, villageResources).stream()
                    .map(VillageItemMapper.INSTANCE::toVillageItem)
                    .toList();
        };
    }

}
