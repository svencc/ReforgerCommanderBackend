package com.recom.service.mapentitygenerator;

import com.recom.commons.model.maprendererpipeline.dataprovider.structure.StructureItem;
import com.recom.commons.model.maprendererpipeline.dataprovider.structure.StructureProvidable;
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
public class StructureProviderGenerator implements SpacialItemProviderGenerator<StructureProvidable> {

    @Nullable
    private List<StructureItem> cachedStructures = null;
    @NonNull
    private final MapStructurePersistenceLayer mapStructurePersistenceLayer;
    @NonNull
    private final ConfigurationValueProvider configurationValueProvider;


    @NonNull
    public StructureProvidable generate(@NonNull final GameMap gameMap) {
        return () -> {
            final List<String> structureResources = configurationValueProvider.queryValue(gameMap, ConfigurationDescriptorProvider.CLUSTERING_VILLAGE_RESOURCES_LIST);

            if (cachedStructures == null) {
                cachedStructures = mapStructurePersistenceLayer.projectStructureItemByMapNameAndResourceNameIn(gameMap, structureResources).parallelStream()
                        .map(structureItem -> StructureItem.builder()
                                .coordinateX(structureItem.getCoordinateX())
                                .coordinateY(structureItem.getCoordinateY())
                                .build()
                        )
                        .toList();
            }

            return cachedStructures;
        };
    }

}
