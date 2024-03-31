package com.recom.persistence.map.chunk.structure;

import com.recom.entity.map.GameMap;
import com.recom.entity.map.SquareKilometerStructureChunk;
import com.recom.event.listener.generic.generic.MapRelatedEntityPersistable;
import com.recom.event.listener.topography.ChunkCoordinate;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MapStructureChunkPersistenceLayer implements MapRelatedEntityPersistable<SquareKilometerStructureChunk> {

    @NonNull
    private final MapStructureChunkRepository mapStructureChunkRepository;


    @NonNull
    public SquareKilometerStructureChunk save(@NonNull final SquareKilometerStructureChunk chunk) {
        return mapStructureChunkRepository.save(chunk);
    }

    public Integer deleteMapEntities(@NonNull final GameMap gameMap) {
        return mapStructureChunkRepository.deleteByGameMap(gameMap);
    }

    @NonNull
    public List<SquareKilometerStructureChunk> findByGameMap(@NonNull final GameMap gameMap) {
        return mapStructureChunkRepository.findByGameMap(gameMap);
    }

    @NonNull
    public Optional<SquareKilometerStructureChunk> findByGameMapAndCoordinate(
            @NonNull final GameMap gameMap,
            @NonNull final ChunkCoordinate chunkCoordinate
    ) {
        return mapStructureChunkRepository.findByGameMapAndSquareCoordinateXAndSquareCoordinateY(gameMap, chunkCoordinate.x(), chunkCoordinate.z());
    }

}