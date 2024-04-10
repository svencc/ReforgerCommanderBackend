package com.recom.service.messagebus.chunkscanrequest;

import com.recom.dto.map.scanner.MapChunkScanRequestDto;
import com.recom.dto.map.scanner.TransactionIdentifierDto;
import com.recom.dto.message.MessageType;
import com.recom.entity.map.ChunkStatus;
import com.recom.entity.map.GameMap;
import com.recom.entity.map.SquareKilometerTopographyChunk;
import com.recom.event.listener.util.ChunkHelper;
import com.recom.model.message.MessageContainer;
import com.recom.model.message.SingleMessage;
import com.recom.persistence.map.GameMapPersistenceLayer;
import com.recom.persistence.map.chunk.topography.MapTopographyChunkPersistenceLayer;
import com.recom.service.messagebus.MessageBusService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Slf4j
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

        log.debug("Requesting map topography chunk scan for map: {}", mapName);
        gameMapPersistenceLayer.findByName(mapName)
                .ifPresent(this::requestMapTopographyChunkScan);
    }

    @NonNull
    private Optional<SquareKilometerTopographyChunk> getChunkToScanNext(@NonNull final GameMap gameMap) {
        final List<SquareKilometerTopographyChunk> remainingChunksToScan = mapTopographyChunkPersistenceLayer.findByGameMap(gameMap).stream()
                .filter(chunk -> {
                    final boolean isStale = Optional.ofNullable(chunk.getLastUpdate()).map(latestUpdate -> LocalDateTime.now().plusMinutes(5).isAfter(latestUpdate)).orElse(false);
                    return chunk.getStatus() == ChunkStatus.OPEN ||
                            (chunk.getStatus() == ChunkStatus.REQUESTED && !isStale);
                })
                .sorted(Comparator.comparing(SquareKilometerTopographyChunk::getLastUpdate, Comparator.nullsFirst(Comparator.naturalOrder())))
                .toList();

        if (remainingChunksToScan.isEmpty()) {
            log.debug("No chunks to scan for map: {}", gameMap.getName());
            return Optional.empty();
        } else {
            final SquareKilometerTopographyChunk randomChunk = remainingChunksToScan.get(random.nextInt(remainingChunksToScan.size()));
            log.debug("Found chunk to scan: {}", randomChunk);

            return Optional.of(randomChunk);
        }
    }

    @Synchronized
    @Transactional(readOnly = false)
    public void requestMapTopographyChunkScan(@NonNull final GameMap gameMap) {
        getChunkToScanNext(gameMap)
                .ifPresent(nextChunk -> {
                    log.debug("Requesting topography chunk scan for chunk: {}", nextChunk);
                    messageBusService.sendMessage(MessageContainer.builder()
                            .gameMap(gameMap)
                            .messages(List.of(
                                    SingleMessage.builder()
                                            .messageType(MessageType.REQUEST_MAP_TOPOGRAPHY_CHUNK)
                                            .payload(MapChunkScanRequestDto.builder()
                                                    .mapName(gameMap.getName())
                                                    .chunkCoordinateX(nextChunk.getSquareCoordinateX())
                                                    .chunkCoordinateY(nextChunk.getSquareCoordinateY())
                                                    .build()
                                            )
                                            .build()
                            ))
                            .build()
                    );

                    // Update the chunk status to requested
                    nextChunk.setStatus(ChunkStatus.REQUESTED);
                    nextChunk.setLastUpdate(LocalDateTime.now());
                    mapTopographyChunkPersistenceLayer.save(nextChunk);
                });
    }

}
