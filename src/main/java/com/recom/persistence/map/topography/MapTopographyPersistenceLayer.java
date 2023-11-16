package com.recom.persistence.map.topography;

import com.recom.entity.GameMap;
import com.recom.entity.MapTopography;
import com.recom.event.listener.generic.generic.MapEntityPersistable;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MapTopographyPersistenceLayer implements MapEntityPersistable<MapTopography> {

    @NonNull
    private final MapTopographyRepository mapTopographyRepository;

    @NonNull
    public List<MapTopography> saveAll(@NonNull List<MapTopography> distinctEntities) {
        return mapTopographyRepository.saveAll(distinctEntities);
    }

    public Integer deleteMapEntities(@NonNull final GameMap gameMap) {
        return mapTopographyRepository.deleteByGameMap(gameMap);
    }

}