package com.recom.event.listener;

import com.recom.dto.map.scanner.topography.MapTopographyEntityDto;
import com.recom.dto.map.scanner.topography.TransactionalMapTopographyEntityPackageDto;
import com.recom.entity.map.GameMap;
import com.recom.entity.map.SquareKilometerTopographyChunk;
import com.recom.event.event.async.map.addmappackage.AddMapTopographyPackageAsyncEvent;
import com.recom.event.event.async.map.commit.CommitMapTopographyTransactionAsyncEvent;
import com.recom.event.event.async.map.open.OpenMapTopographyTransactionAsyncEvent;
import com.recom.event.listener.generic.maprelated.TransactionalMapRelatedPackageEventListenerTemplate;
import com.recom.event.listener.topography.ChunkCoordinate;
import com.recom.event.listener.topography.ChunkDimensions;
import com.recom.event.listener.topography.ChunkHelper;
import com.recom.model.map.MapTransaction;
import com.recom.persistence.map.GameMapPersistenceLayer;
import com.recom.persistence.map.topography.MapTopographyChunkPersistenceLayer;
import com.recom.service.SerializationService;
import com.recom.service.map.MapTransactionValidatorService;
import com.recom.service.messagebus.MapTopographyChunkScanRequestNotificationService;
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
    @NonNull
    private final MapTopographyChunkScanRequestNotificationService mapTopographyChunkScanRequestNotificationService;


    public MapTopographyEntityScannerTransactionEventListener(
            @NonNull final TransactionTemplate transactionTemplate,
            @NonNull final MapTopographyChunkPersistenceLayer entityPersistenceLayer,
            @NonNull final MapTransactionValidatorService<MapTopographyEntityDto, TransactionalMapTopographyEntityPackageDto> mapTransactionValidator,
            @NonNull final GameMapPersistenceLayer gameMapPersistenceLayer,
            @NonNull final ApplicationEventPublisher applicationEventPublisher,
            @NonNull final SerializationService serializationService,
            @NonNull final MapTopographyChunkScanRequestNotificationService mapTopographyChunkScanRequestNotificationService
    ) {
        super(transactionTemplate, entityPersistenceLayer, mapTransactionValidator, gameMapPersistenceLayer, applicationEventPublisher);

        this.serializationService = serializationService;
        this.mapTopographyChunkScanRequestNotificationService = mapTopographyChunkScanRequestNotificationService;
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
        mapTopographyChunkScanRequestNotificationService.requestMapTopographyChunkScan(event.getTransactionIdentifierDto());
    }

    @NonNull
    @Override
    protected SquareKilometerTopographyChunk mapTransactionToEntity(
            @NonNull final String sessionIdentifier,
            @NonNull final GameMap gameMap,
            @NonNull final List<TransactionalMapTopographyEntityPackageDto> packages
    ) {
        final ChunkCoordinate chunkCoordinate = ChunkHelper.extractChunkCoordinateFromSessionIdentifier(sessionIdentifier);
        final ChunkDimensions chunkDimensions = ChunkHelper.determineChunkDimensions(packages);

        final float[][] chunkedDem = new float[chunkDimensions.x()][chunkDimensions.z()];
        packages.stream()
                .flatMap((entityPackage) -> entityPackage.getEntities().stream())
                .forEach((final MapTopographyEntityDto packageDto) -> {
                    final int x = packageDto.getCoordinates().get(0).intValue();
                    final float y = packageDto.getCoordinates().get(1).floatValue();
                    final int z = packageDto.getCoordinates().get(2).intValue();
                    chunkedDem[x][z] = y;
                });

        try {
            final SquareKilometerTopographyChunk squareKilometerTopographyChunk = SquareKilometerTopographyChunk.builder()
                    .gameMap(gameMap)
                    .squareCoordinateX(chunkCoordinate.x())
                    .squareCoordinateY(chunkCoordinate.z())
                    .data(serializationService.serializeObject(chunkedDem).toByteArray())
                    .build();

            gameMap.getTopographyChunks().add(squareKilometerTopographyChunk);

            return squareKilometerTopographyChunk;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
