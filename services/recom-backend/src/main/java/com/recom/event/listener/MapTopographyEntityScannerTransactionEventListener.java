package com.recom.event.listener;

import com.recom.dto.map.scanner.topography.MapTopographyEntityDto;
import com.recom.dto.map.scanner.topography.TransactionalMapTopographyEntityPackageDto;
import com.recom.entity.map.GameMap;
import com.recom.entity.map.SquareKilometerTopographyChunk;
import com.recom.event.event.async.map.addmappackage.AddMapTopographyPackageAsyncEvent;
import com.recom.event.event.async.map.commit.CommitMapTopographyTransactionAsyncEvent;
import com.recom.event.event.async.map.open.OpenMapTopographyTransactionAsyncEvent;
import com.recom.event.listener.generic.maprelated.TransactionalMapRelatedPackageEventListenerTemplate;
import com.recom.model.map.MapTransaction;
import com.recom.persistence.map.GameMapPersistenceLayer;
import com.recom.persistence.map.topography.MapLocatedTopographyPersistenceLayer;
import com.recom.service.SerializationService;
import com.recom.service.map.MapTransactionValidatorService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class MapTopographyEntityScannerTransactionEventListener extends TransactionalMapRelatedPackageEventListenerTemplate<TransactionalMapTopographyEntityPackageDto, SquareKilometerTopographyChunk, MapTopographyEntityDto> {

    @NonNull
    private final SerializationService serializationService;

    public MapTopographyEntityScannerTransactionEventListener(
            @NonNull final TransactionTemplate transactionTemplate,
            @NonNull final MapLocatedTopographyPersistenceLayer entityPersistenceLayer,
            @NonNull final MapTransactionValidatorService<MapTopographyEntityDto, TransactionalMapTopographyEntityPackageDto> mapTransactionValidator,
            @NonNull final GameMapPersistenceLayer gameMapPersistenceLayer,
            @NonNull final ApplicationEventPublisher applicationEventPublisher,
            @NonNull final SerializationService serializationService
    ) {
        super(transactionTemplate, entityPersistenceLayer, mapTransactionValidator, gameMapPersistenceLayer, applicationEventPublisher);

        this.serializationService = serializationService;
    }

    @Async("AsyncMapTopographyTransactionExecutor")
    @EventListener(classes = OpenMapTopographyTransactionAsyncEvent.class)
    public void handleOpenTransactionEvent(@NonNull final OpenMapTopographyTransactionAsyncEvent event) {
        debugEvent(event);
        handleOpenTransaction(event.getTransactionIdentifierDto());
        Map<String, MapTransaction<MapTopographyEntityDto, TransactionalMapTopographyEntityPackageDto>> transactions1 = getTransactions();
    }

    @Async("AsyncMapTopographyTransactionExecutor")
    @EventListener(classes = AddMapTopographyPackageAsyncEvent.class)
    public void handleAddMapPackageEvent(@NonNull final AddMapTopographyPackageAsyncEvent event) {
        traceEvent(event);
        handleAddMapPackage(event.getTransactionalMapEntityPackage());
    }

    @Async("AsyncMapTopographyTransactionExecutor")
    @EventListener(classes = CommitMapTopographyTransactionAsyncEvent.class)
    public void handleCommitTransactionEvent(@NonNull final CommitMapTopographyTransactionAsyncEvent event) {
        debugEvent(event);
        handleCommitTransaction(event.getTransactionIdentifierDto());
    }

    @NonNull
    @Override
    protected SquareKilometerTopographyChunk mapTransactionToEntity(
            @NonNull final String sessionIdentifier,
            @NonNull final GameMap gameMap,
            @NonNull final List<TransactionalMapTopographyEntityPackageDto> packages
    ) {
        final String[] additionalInformation = sessionIdentifier.split("#####");
        // assert additionalInformation size 2

        final String mapName = additionalInformation[0];
        final String chunkXZCoordinate = additionalInformation[1];


        final String[] chunkCoordinates = chunkXZCoordinate.split(",");
        // assert chunkCoordinates size 2
        final String chunkCoordinatesX = chunkCoordinates[0];
        final String chunkCoordinatesZ = chunkCoordinates[1];

        final long chunkX = Integer.parseInt(chunkCoordinatesX);
        final long chunkZ = Integer.parseInt(chunkCoordinatesZ);

        // assertions mit den Optionals
        final int dimensionX = packages.stream()
                .flatMap((entityPackage) -> entityPackage.getEntities().stream())
                .map(MapTopographyEntityDto::getCoordinates)
                .mapToInt((coordinates) -> coordinates.get(0).intValue())
                .max()
                .orElseThrow(() -> new IllegalArgumentException("No max X found!"));

        final int dimensionZ = packages.stream()
                .flatMap((entityPackage) -> entityPackage.getEntities().stream())
                .map(MapTopographyEntityDto::getCoordinates)
                .mapToInt((coordinates) -> coordinates.get(2).intValue())
                .max()
                .orElseThrow(() -> new IllegalArgumentException("No max X found!"));

        final float[][] chunkedDem = new float[dimensionX][dimensionZ];

        packages.stream()
                .flatMap((entityPackage) -> entityPackage.getEntities().stream())
                .forEach((final MapTopographyEntityDto packageDto) -> {
                    final int x = packageDto.getCoordinates().get(0).intValue();
                    final float y = packageDto.getCoordinates().get(1).floatValue();
                    final int z = packageDto.getCoordinates().get(3).intValue();
                    chunkedDem[x][z] = y;
                });

        try {
            final SquareKilometerTopographyChunk squareKilometerTopographyChunk = SquareKilometerTopographyChunk.builder()
                    .gameMap(gameMap)
                    .squareCoordinateX(chunkX)
                    .squareCoordinateY(chunkZ)
                    .data(serializationService.serializeObject(chunkedDem).toByteArray())
                    .build();

            gameMap.getTopographyChunks().add(squareKilometerTopographyChunk);

            return squareKilometerTopographyChunk;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
