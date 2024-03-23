package com.recom.service.messagebus;

import com.recom.dto.map.scanner.MapTopographyChunkScanRequestDto;
import com.recom.dto.map.scanner.TransactionIdentifierDto;
import com.recom.dto.message.MessageType;
import com.recom.entity.map.ChunkStatus;
import com.recom.entity.map.GameMap;
import com.recom.entity.map.SquareKilometerTopographyChunk;
import com.recom.event.listener.topography.ChunkCoordinate;
import com.recom.event.listener.topography.ChunkHelper;
import com.recom.model.message.MessageContainer;
import com.recom.model.message.SingleMessage;
import com.recom.persistence.map.GameMapPersistenceLayer;
import com.recom.persistence.map.topography.MapTopographyChunkPersistenceLayer;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class MapTopographyChunkScanRequestNotificationService {

    @NonNull
    private final MessageBusService messageBusService;
    @NonNull
    private final MapTopographyChunkPersistenceLayer mapTopographyChunkPersistenceLayer;
    @NonNull
    private final GameMapPersistenceLayer gameMapPersistenceLayer;
    @NonNull
    private final Random random = new Random();


    public void requestMapTopographyChunkScan(@NonNull final TransactionIdentifierDto transactionIdentifierDto) {
        final String sessionIdentifier = transactionIdentifierDto.getSessionIdentifier();
        final String mapName = ChunkHelper.extractFromSessionIdentifier(sessionIdentifier).mapName();

        gameMapPersistenceLayer.findByName(mapName)
                .ifPresent(this::requestMapTopographyChunkScan);
    }

    @NonNull
    public Optional<ChunkCoordinate> getChunkCoordinateToScanNext(@NonNull final GameMap gameMap) {
        final List<SquareKilometerTopographyChunk> remainingChunksToScan = mapTopographyChunkPersistenceLayer.findByGameMap(gameMap).stream()
                .filter(chunk -> {
                    final boolean isStale = Optional.ofNullable(chunk.getLastUpdate()).map(latestUpdate -> LocalDateTime.now().minusMinutes(10).isAfter(latestUpdate)).orElse(false);
                    return chunk.getStatus() == ChunkStatus.PENDING ||
                            (chunk.getStatus() == ChunkStatus.REQUESTED && isStale);
                })
                .toList();

        if (remainingChunksToScan.isEmpty()) {
            return Optional.empty();
        } else {
            final SquareKilometerTopographyChunk randomChunk = remainingChunksToScan.get(random.nextInt(remainingChunksToScan.size()));
            final int nextChunkCoordinateX = randomChunk.getSquareCoordinateX().intValue();
            final int nextChunkCoordinateY = randomChunk.getSquareCoordinateY().intValue();

            return Optional.of(new ChunkCoordinate(nextChunkCoordinateX, nextChunkCoordinateY));
        }
    }

    // 1. on map creation: MapExistsController.create
    // 2. MapExistsController: mapExists
    // 3. CommitMapTopographyTransactionAsyncEvent MapTopographyEntityScannerTransactionEventListener.handleCommitTransactionEvent
    public void requestMapTopographyChunkScan(@NonNull final GameMap gameMap) {
        getChunkCoordinateToScanNext(gameMap)
                .ifPresent(chunkCoordinate -> {
                    messageBusService.sendMessage(MessageContainer.builder()
                            .gameMap(gameMap)
                            .messages(List.of(
                                    SingleMessage.builder()
                                            .messageType(MessageType.REQUEST_MAP_CHUNK)
                                            .payload(MapTopographyChunkScanRequestDto.builder()
                                                    .mapName(gameMap.getName())
                                                    .chunkCoordinateX(chunkCoordinate.x())
                                                    .chunkCoordinateY(chunkCoordinate.z())
                                                    .build()
                                            )
                                            .build()
                            ))
                            .build()
                    );
                });
    }

//    public void requestMapTopographyChunkScan(
//            @NonNull final GameMap gameMap,
//            @NonNull final ChunkCoordinate chunkCoordinate
//    ) {
//        messageBusService.sendMessage(MessageContainer.builder()
//                .gameMap(gameMap)
//                .messages(List.of(
//                        SingleMessage.builder()
//                                .messageType(MessageType.REQUEST_MAP_CHUNK)
//                                .payload(MapTopographyChunkScanRequestDto.builder()
//                                        .mapName(gameMap.getName())
//                                        .chunkCoordinateX(chunkCoordinate.x())
//                                        .chunkCoordinateY(chunkCoordinate.z())
//                                        .build()
//                                )
//                                .build()
//                ))
//                .build()
//        );
//    }

}
