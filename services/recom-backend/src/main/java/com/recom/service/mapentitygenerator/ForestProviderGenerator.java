package com.recom.service.mapentitygenerator;

import com.recom.commons.model.maprendererpipeline.dataprovider.forest.ForestItem;
import com.recom.commons.model.maprendererpipeline.dataprovider.forest.ForestProvidable;
import com.recom.commons.model.maprendererpipeline.dataprovider.structure.StructureItem;
import com.recom.entity.map.GameMap;
import com.recom.persistence.map.structure.MapStructurePersistenceLayer;
import com.recom.service.configuration.ConfigurationDescriptorProvider;
import com.recom.service.configuration.ConfigurationValueProvider;
import jakarta.annotation.Nullable;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
    private final List<CompletableFuture<List<ForestItem>>> generatedFutures = new ArrayList<>();
    @Nullable
    private Future<?> generator;


    @NonNull
    @Synchronized
    public ForestProvidable generateProvider(@NonNull final GameMap gameMap) {
        return () -> {
            if (cachedForestItems == null && generator == null) {
                final CompletableFuture<List<ForestItem>> future = new CompletableFuture<>();
                generatedFutures.add(future);

                generator = Executors.newVirtualThreadPerTaskExecutor().submit(() -> {
                    cachedForestItems = mapStructurePersistenceLayer.projectStructureItemByMapNameAndResourceNameIn(gameMap, configurationValueProvider.queryValue(gameMap, ConfigurationDescriptorProvider.CLUSTERING_FOREST_RESOURCES_LIST)).parallelStream()
                            .map(structureItem -> ForestItem.builder()
                                    .coordinateX(structureItem.getCoordinateX())
                                    .coordinateY(structureItem.getCoordinateY())
                                    .build()
                            )
                            .toList();
                    generatedFutures.forEach(localFuture -> localFuture.complete(cachedForestItems));
                });

                return future;
            } else {
                final CompletableFuture<List<ForestItem>> future = new CompletableFuture<>();
                future.complete(cachedForestItems);

                return future;
            }
        };
    }

}
