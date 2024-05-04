package com.recom.service.mapentitygenerator;

import com.recom.commons.model.maprendererpipeline.dataprovider.forest.ForestItem;
import com.recom.commons.model.maprendererpipeline.dataprovider.forest.ForestProvidable;
import com.recom.entity.map.GameMap;
import com.recom.persistence.map.structure.MapStructurePersistenceLayer;
import com.recom.service.configuration.ConfigurationDescriptorProvider;
import com.recom.service.configuration.ConfigurationValueProvider;
import jakarta.annotation.Nullable;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ForestProviderGenerator implements SpacialItemProviderGenerator<ForestProvidable> {

    @Nullable
    private List<ForestItem> cachedForestItems = null;
    @NonNull
    private final MapStructurePersistenceLayer mapStructurePersistenceLayer;
    @NonNull
    private final ConfigurationValueProvider configurationValueProvider;


    @NonNull
    public ForestProvidable generate(@NonNull final GameMap gameMap) {
        return () -> {
            final List<String> forestResources = configurationValueProvider.queryValue(gameMap, ConfigurationDescriptorProvider.CLUSTERING_FOREST_RESOURCES_LIST);

            if (cachedForestItems == null) {
                cachedForestItems = mapStructurePersistenceLayer.projectStructureItemByMapNameAndResourceNameIn(gameMap, forestResources).parallelStream()
                        .map(structureItem -> ForestItem.builder()
                                .coordinateX(structureItem.getCoordinateX())
                                .coordinateY(structureItem.getCoordinateY())
                                .build()
                        )
                        .toList();
            }

            return cachedForestItems;
        };
    }

}
