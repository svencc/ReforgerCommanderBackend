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
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
@Service
@RequiredArgsConstructor
public class ForestProviderGenerator implements SpacialItemProviderGenerator<ForestProvidable> {

    @Nullable
    private final HashMap<String, List<ForestItem>> cachedForestItems =  new HashMap<>();
    @NonNull
    private final MapStructurePersistenceLayer mapStructurePersistenceLayer;
    @NonNull
    private final ConfigurationValueProvider configurationValueProvider;
    @NonNull
    private final HashMap<String, List<CompletableFuture<List<ForestItem>>>> generatedFutures = new HashMap<>();
    @NonNull
    private final HashMap<String, Future<?>> generator = new HashMap<>();


    @NonNull
    @Synchronized
    public ForestProvidable provideFutureGenerator(@NonNull final GameMap gameMap) {
        return () -> {
            if (!cachedForestItems.containsKey(gameMap.getName()) && !generator.containsKey(gameMap.getName())) {
                final CompletableFuture<List<ForestItem>> future = new CompletableFuture<>();

                // da muss ein Helper her
                if (generatedFutures.containsKey(gameMap.getName())) {
                    generatedFutures.get(gameMap.getName()).add(future);
                } else {
                    generatedFutures.put(gameMap.getName(), new ArrayList<>());
                    generatedFutures.get(gameMap.getName()).add(future);
                }
                // da muss ein Helper her

                generator.put(gameMap.getName(), Executors.newVirtualThreadPerTaskExecutor().submit(() -> {
                    try {
                        cachedForestItems.put(gameMap.getName(), mapStructurePersistenceLayer.projectStructureItemByMapNameAndResourceNameIn(gameMap, configurationValueProvider.queryValue(gameMap, ConfigurationDescriptorProvider.CLUSTERING_FOREST_RESOURCES_LIST)).parallelStream()
                                .map(structureItem -> ForestItem.builder()
                                        .coordinateX(structureItem.getCoordinateX())
                                        .coordinateY(structureItem.getCoordinateY())
                                        .build()
                                )
                                .toList());
                        generatedFutures.get(gameMap.getName()).forEach(localFuture -> localFuture.complete(cachedForestItems.get(gameMap.getName())));
                    } catch (final Throwable t) {
                        log.error("Error while generating forest items", t);
                    }
                }));

                return future;
            } else {
                final CompletableFuture<List<ForestItem>> future = new CompletableFuture<>();
                future.complete(cachedForestItems.get(gameMap.getName()));

                return future;
            }
        };
    }

}
