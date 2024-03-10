package com.recom.service;

import com.recom.commons.model.maprendererpipeline.dataprovider.village.StructureProvidable;
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
public class StructureProviderGenerator {

    @NonNull
    private final MapStructurePersistenceLayer mapStructurePersistenceLayer;
    @NonNull
    private final ConfigurationValueProvider configurationValueProvider;


    @NonNull
    public StructureProvidable generate(@NonNull final GameMap gameMap) {
        return () -> {
            final List<String> structureResources = configurationValueProvider.queryValue(gameMap, ConfigurationDescriptorProvider.CLUSTERING_VILLAGE_RESOURCES_LIST);

            return mapStructurePersistenceLayer.findAllByMapNameAndResourceNameIn(gameMap, structureResources).stream()
                    .map(VillageItemMapper.INSTANCE::toVillageItem)
                    .toList();
        };
    }

}
