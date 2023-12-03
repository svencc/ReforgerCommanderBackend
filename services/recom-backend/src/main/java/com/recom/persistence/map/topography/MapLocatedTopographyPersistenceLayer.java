package com.recom.persistence.map.topography;

import com.recom.entity.map.GameMap;
import com.recom.entity.map.MapTopography;
import com.recom.event.listener.generic.generic.MapRelatedEntityPersistable;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MapLocatedTopographyPersistenceLayer implements MapRelatedEntityPersistable<MapTopography> {

    @NonNull
    private final MapTopographyRepository mapTopographyRepository;

    @NonNull
    public MapTopography save(@NonNull MapTopography distinctEntities) {
        return mapTopographyRepository.save(distinctEntities);
    }

    public Integer deleteMapEntities(@NonNull final GameMap gameMap) {
        return mapTopographyRepository.deleteByGameMap(gameMap);
    }

    @NonNull
    public Optional<MapTopography> findByGameMap(@NonNull final GameMap gameMap) {
        return mapTopographyRepository.findByGameMap(gameMap);
    }

}