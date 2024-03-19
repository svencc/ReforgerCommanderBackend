package com.recom.service.messagebus;

import com.recom.dto.map.scanner.MapChunkScanRequestDto;
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
import java.util.Random;

@Service
@RequiredArgsConstructor
public class MapScanNotificationService {

    @NonNull
    private final MessageBusService messageBusService;
    @NonNull
    private final MapTopographyChunkPersistenceLayer mapTopographyChunkPersistenceLayer;
    @NonNull
    private final GameMapPersistenceLayer gameMapPersistenceLayer;
    @NonNull
    private final Random random = new Random();


    public boolean notifyMapScan(@NonNull final TransactionIdentifierDto transactionIdentifierDto) {
        final String sessionIdentifier = transactionIdentifierDto.getSessionIdentifier();
        final String mapName = ChunkHelper.extractFromSessionIdentifier(sessionIdentifier).mapName();

        return gameMapPersistenceLayer.findByName(mapName).map((final GameMap gameMap) -> {
                    List<SquareKilometerTopographyChunk> remainingChunksToScan = mapTopographyChunkPersistenceLayer.findByGameMap(gameMap).stream()
                            .filter(chunk -> {
                                final boolean isStale = LocalDateTime.now().minusMinutes(10).isAfter(chunk.getLastUpdate());
                                return (chunk.getStatus() == ChunkStatus.PENDING) ||
                                        (chunk.getStatus() == ChunkStatus.REQUESTED && isStale);
                            })
                            .toList();

                    final SquareKilometerTopographyChunk randomChunk = remainingChunksToScan.get(random.nextInt(remainingChunksToScan.size()));
                    final int nextChunkCoordinateX = randomChunk.getSquareCoordinateX().intValue();
                    final int nextChunkCoordinateY = randomChunk.getSquareCoordinateY().intValue();
                    final ChunkCoordinate chunkCoordinate = new ChunkCoordinate(nextChunkCoordinateX, nextChunkCoordinateY);

                    notifyMapScan(gameMap, chunkCoordinate);

                    return true;
                })
                .orElse(false);
    }

    public void notifyMapScan(
            @NonNull final GameMap gameMap,
            @NonNull final ChunkCoordinate chunkCoordinate
    ) {
        messageBusService.sendMessage(MessageContainer.builder()
                .gameMap(gameMap)
                .messages(List.of(
                        SingleMessage.builder()
                                .messageType(MessageType.REQUEST_MAP_CHUNK)
                                .payload(MapChunkScanRequestDto.builder()
                                        .mapName(gameMap.getName())
                                        .chunkCoordinateX((int) chunkCoordinate.x())
                                        .chunkCoordinateY((int) chunkCoordinate.z())
                                        .build()
                                )
                                .build()
                ))
                .build()
        );
    }

}
