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
import lombok.Synchronized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
@RequiredArgsConstructor
public class StructureProviderGenerator implements SpacialItemProviderGenerator<StructureProvidable> {
    private static final Logger log = LoggerFactory.getLogger(StructureProviderGenerator.class); // @TODO: glaub SpacialItemProviderGenerator können wir einfach gegen ein CompleteableFuture tauschen!!!

    @Nullable
    private List<StructureItem> cachedStructureItems = null;
    @NonNull
    private final MapStructurePersistenceLayer mapStructurePersistenceLayer;
    @NonNull
    private final ConfigurationValueProvider configurationValueProvider;
    @NonNull
    private final List<CompletableFuture<List<StructureItem>>> generatedFutures = new ArrayList<>();
    @Nullable
    private Future<?> generator;


    @NonNull
    @Synchronized
    public StructureProvidable generateProvider(@NonNull final GameMap gameMap) {
        return () -> {
            if (cachedStructureItems == null && generator == null) {
                final CompletableFuture<List<StructureItem>> future = new CompletableFuture<>();
                generatedFutures.add(future);

                generator = Executors.newVirtualThreadPerTaskExecutor().submit(() -> {
                    try {

                    cachedStructureItems = mapStructurePersistenceLayer.projectStructureItemByMapNameAndResourceNameIn(gameMap, configurationValueProvider.queryValue(gameMap, ConfigurationDescriptorProvider.CLUSTERING_VILLAGE_RESOURCES_LIST)).parallelStream()
                            .map(structureItem -> StructureItem.builder()
                                    .coordinateX(structureItem.getCoordinateX())
                                    .coordinateY(structureItem.getCoordinateY())
                                    .build()
                            )
                            .toList();
                    generatedFutures.forEach(localFuture -> localFuture.complete(cachedStructureItems));
                    } catch (Throwable t) {
                        t.printStackTrace();
                        log.error("Error while generating structure items", t);
                    }
                });

                return future;
            } else {
                final CompletableFuture<List<StructureItem>> future = new CompletableFuture<>();
                future.complete(cachedStructureItems);

                return future;
            }
        };
    }

}
