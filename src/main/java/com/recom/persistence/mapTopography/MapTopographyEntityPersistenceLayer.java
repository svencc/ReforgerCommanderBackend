package com.recom.persistence.mapTopography;

import com.recom.entity.MapTopographyEntity;
import com.recom.event.listener.generic.MapEntityPersistable;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MapTopographyEntityPersistenceLayer implements MapEntityPersistable<MapTopographyEntity> {

    @NonNull
    private final MapTopographyEntityRepository mapTopographyEntityRepository;

    @NonNull
    public List<MapTopographyEntity> saveAll(@NonNull List<MapTopographyEntity> distinctEntities) {
        return mapTopographyEntityRepository.saveAll(distinctEntities);
    }

    public Integer deleteMapEntities(@NonNull final String mapName) {
        return mapTopographyEntityRepository.deleteByMapName(mapName);
    }

    @Cacheable(cacheNames = "MapTopographyEntityPersistenceLayer.countEntitiesByMapName")
    public Integer countEntitiesByMapName(@NonNull final String mapName) {
        return mapTopographyEntityRepository.countByMapName(mapName);
    }

    @NonNull
    public List<MapTopographyEntity> findAllByMapNameOrdered(@NonNull final String mapName) {
        return mapTopographyEntityRepository.findAllByMapNameOrderByCoordinateXAscCoordinateZAsc(mapName);
    }

}