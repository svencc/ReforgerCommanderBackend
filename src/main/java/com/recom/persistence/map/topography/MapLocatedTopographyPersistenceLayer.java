package com.recom.persistence.map.topography;

import com.recom.entity.GameMap;
import com.recom.entity.MapTopography;
import com.recom.event.listener.generic.generic.MapRelatedEntityPersistable;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

}