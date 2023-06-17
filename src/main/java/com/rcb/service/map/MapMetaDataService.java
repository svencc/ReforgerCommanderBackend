package com.rcb.service.map;

import com.rcb.dto.map.meta.MapMetaDto;
import com.rcb.dto.map.meta.MapMetaListDto;
import com.rcb.repository.mapEntity.MapEntityPersistenceLayer;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MapMetaDataService {

    @NonNull
    private final MapEntityPersistenceLayer mapEntityPersistenceLayer;

    @NonNull
    @Cacheable(cacheNames = "MapMetaDataService.provideMapList")
    public MapMetaListDto provideMapMetaList() {
        return MapMetaListDto.builder()
                .maps(mapEntityPersistenceLayer.findAllMapNames().stream()
                        .map((@NonNull final String mapName) -> MapMetaDto.builder()
                                .mapName(mapName)
                                .entitiesCount(mapEntityPersistenceLayer.countEntitiesByMapName(mapName))
                                .utilizedClasses(mapEntityPersistenceLayer.utilizedClassesByMapName(mapName))
                                .utilizedResources(mapEntityPersistenceLayer.utilizedResourcesByMapName(mapName))
                                .utilizedPrefabs(mapEntityPersistenceLayer.utilizedPrefabsByMapName(mapName))
                                .utilizedMapMetaTypes(mapEntityPersistenceLayer.utilizedMapMetaTypeByMapName(mapName))
                                .namedEntities(mapEntityPersistenceLayer.utilizedNamedEntitiesByMapName(mapName))
                                .build()
                        )
                        .toList())
                .build();
    }

}
