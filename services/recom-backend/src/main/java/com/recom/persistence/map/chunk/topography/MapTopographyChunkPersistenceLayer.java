package com.recom.persistence.map.chunk.topography;

import com.recom.entity.map.GameMap;
import com.recom.entity.map.SquareKilometerTopographyChunk;
import com.recom.event.listener.generic.generic.MapRelatedEntityPersistable;
import com.recom.event.listener.util.ChunkCoordinate;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MapTopographyChunkPersistenceLayer implements MapRelatedEntityPersistable<SquareKilometerTopographyChunk> {

    @NonNull
    private final MapTopographyChunkRepository mapTopographyChunkRepository;


    @NonNull
    public SquareKilometerTopographyChunk save(@NonNull final SquareKilometerTopographyChunk chunk) {
        return mapTopographyChunkRepository.save(chunk);
    }

    public Integer deleteMapEntities(@NonNull final GameMap gameMap) {
        return mapTopographyChunkRepository.deleteByGameMap(gameMap);
    }

    @NonNull
    public List<SquareKilometerTopographyChunk> findByGameMap(@NonNull final GameMap gameMap) {
        return mapTopographyChunkRepository.findByGameMap(gameMap);
    }

    @NonNull
    public Optional<SquareKilometerTopographyChunk> findByGameMapAndCoordinate(
            @NonNull final GameMap gameMap,
            @NonNull final ChunkCoordinate chunkCoordinate
    ) {
        return mapTopographyChunkRepository.findByGameMapAndSquareCoordinateXAndSquareCoordinateY(gameMap, chunkCoordinate.x(), chunkCoordinate.z());
    }

}