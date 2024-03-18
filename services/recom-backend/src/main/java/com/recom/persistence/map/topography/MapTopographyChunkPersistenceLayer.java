package com.recom.persistence.map.topography;

import com.recom.entity.map.GameMap;
import com.recom.entity.map.SquareKilometerTopographyChunk;
import com.recom.event.listener.generic.generic.MapRelatedEntityPersistable;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MapTopographyChunkPersistenceLayer implements MapRelatedEntityPersistable<SquareKilometerTopographyChunk> {

    @NonNull
    private final MapTopographyChunkRepository mapTopographyChunkRepository;

    @NonNull
    public SquareKilometerTopographyChunk save(@NonNull SquareKilometerTopographyChunk distinctEntities) {
        return mapTopographyChunkRepository.save(distinctEntities);
    }

    public Integer deleteMapEntities(@NonNull final GameMap gameMap) {
        return mapTopographyChunkRepository.deleteByGameMap(gameMap);
    }

    @NonNull
    public List<SquareKilometerTopographyChunk> findByGameMap(@NonNull final GameMap gameMap) {
        return mapTopographyChunkRepository.findByGameMap(gameMap);
    }

}