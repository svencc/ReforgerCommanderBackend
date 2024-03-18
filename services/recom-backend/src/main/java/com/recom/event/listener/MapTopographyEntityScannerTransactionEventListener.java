package com.recom.event.listener;

import com.recom.dto.map.scanner.MapChunkScanRequestDto;
import com.recom.dto.map.scanner.topography.MapTopographyEntityDto;
import com.recom.dto.map.scanner.topography.TransactionalMapTopographyEntityPackageDto;
import com.recom.dto.message.MessageType;
import com.recom.entity.map.ChunkStatus;
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
import com.recom.model.message.MessageContainer;
import com.recom.model.message.SingleMessage;
import com.recom.persistence.map.GameMapPersistenceLayer;
import com.recom.persistence.map.topography.MapTopographyChunkPersistenceLayer;
import com.recom.service.SerializationService;
import com.recom.service.map.MapTransactionValidatorService;
import com.recom.service.messagebus.MessageBusService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Slf4j
@Component
public class MapTopographyEntityScannerTransactionEventListener extends TransactionalMapRelatedPackageEventListenerTemplate<TransactionalMapTopographyEntityPackageDto, SquareKilometerTopographyChunk, MapTopographyEntityDto> {

    @NonNull
    private final SerializationService serializationService;
    @NonNull
    private final MapTopographyChunkPersistenceLayer mapTopographyChunkPersistenceLayer;
    @NonNull
    private final MessageBusService messageBusService;


    public MapTopographyEntityScannerTransactionEventListener(
            @NonNull final TransactionTemplate transactionTemplate,
            @NonNull final MapTopographyChunkPersistenceLayer entityPersistenceLayer,
            @NonNull final MapTransactionValidatorService<MapTopographyEntityDto, TransactionalMapTopographyEntityPackageDto> mapTransactionValidator,
            @NonNull final GameMapPersistenceLayer gameMapPersistenceLayer,
            @NonNull final ApplicationEventPublisher applicationEventPublisher,
            @NonNull final SerializationService serializationService,
            @NonNull final MapTopographyChunkPersistenceLayer mapTopographyChunkPersistenceLayer,
            @NonNull final MessageBusService messageBusService
    ) {
        super(transactionTemplate, entityPersistenceLayer, mapTransactionValidator, gameMapPersistenceLayer, applicationEventPublisher);

        this.serializationService = serializationService;
        this.mapTopographyChunkPersistenceLayer = mapTopographyChunkPersistenceLayer;
        this.messageBusService = messageBusService;
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

        // @TODO <<<<<<<<<<<<<<<<<<<<<<<< RequestMapChunkService (event.getTransactionIdentifierDto().getSessionIdentifier() mapName) <<<<<<<<<<<<<<<<<<<<<<<<
        final String sessionIdentifier = event.getTransactionIdentifierDto().getSessionIdentifier();
        final String mapName = sessionIdentifier.split("#####")[0]; // @TODO extract to helper!
        gameMapPersistenceLayer.findByName(mapName).ifPresent((final GameMap gameMap) -> {
            // @TODO Nächsten Chunk ermitteln der nicht READY und nicht REQUESTED ist <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            final Random random = new Random();
            List<SquareKilometerTopographyChunk> remainingChunksToScan = mapTopographyChunkPersistenceLayer.findByGameMap(gameMap).stream()
                    .filter(chunk -> {
                        final boolean isStale = LocalDateTime.now().minusMinutes(10).isAfter(chunk.getLastUpdate());
                        return (chunk.getStatus() == ChunkStatus.PENDING) ||
                                (chunk.getStatus() == ChunkStatus.REQUESTED && isStale);
                    })
                    .toList();
            final SquareKilometerTopographyChunk randomChunk = remainingChunksToScan.get(random.nextInt(remainingChunksToScan.size()));
            final Integer nextChunkCoordinateX = randomChunk.getSquareCoordinateX().intValue();
            final Integer nextChunkCoordinateY = randomChunk.getSquareCoordinateY().intValue();

            messageBusService.sendMessage(MessageContainer.builder()
                    .gameMap(gameMap)
                    .messages(List.of(
                            SingleMessage.builder()
                                    .messageType(MessageType.REQUEST_MAP_CHUNK)
                                    .payload(MapChunkScanRequestDto.builder()
                                            .mapName(gameMap.getName())
                                            .chunkCoordinateX(nextChunkCoordinateX)
                                            .chunkCoordinateY(nextChunkCoordinateY)
                                            .build()
                                    )
                                    .build()
                    ))
                    .build()
            );
        // @TODO <<<<<<<<<<<<<<<<<<<<<<<< RequestMapChunkService (event.getTransactionIdentifierDto().getSessionIdentifier() mapName) <<<<<<<<<<<<<<<<<<<<<<<<
        });
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
