package com.recom.service.map;

import com.recom.dto.map.meta.MapMetaDto;
import com.recom.repository.mapEntity.MapEntityPersistenceLayer;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MapMetaDataService {

    @NonNull
    private final MapEntityPersistenceLayer mapEntityPersistenceLayer;

    @NonNull
    @Cacheable(cacheNames = "MapMetaDataService.provideMapList")
    public List<MapMetaDto> provideMapMetaList() {
        return mapEntityPersistenceLayer.findAllMapNames().stream()
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
                .toList();
    }

    @NonNull
    @Cacheable(cacheNames = "MapMetaDataService.provideMap")
    public MapMetaDto provideMapMeta(@NonNull final String mapName) {
        return MapMetaDto.builder()
                .mapName(mapName)
                .entitiesCount(mapEntityPersistenceLayer.countEntitiesByMapName(mapName))
                .utilizedClasses(mapEntityPersistenceLayer.utilizedClassesByMapName(mapName))
                .utilizedResources(mapEntityPersistenceLayer.utilizedResourcesByMapName(mapName))
                .utilizedPrefabs(mapEntityPersistenceLayer.utilizedPrefabsByMapName(mapName))
                .utilizedMapMetaTypes(mapEntityPersistenceLayer.utilizedMapMetaTypeByMapName(mapName))
                .namedEntities(mapEntityPersistenceLayer.utilizedNamedEntitiesByMapName(mapName))
                .build();
    }

    @NonNull
    @Cacheable(cacheNames = "MapMetaDataService.provideAllMapNames")
    public List<String> provideAllMapNames() {
        return mapEntityPersistenceLayer.findAllMapNames();
    }

    @Cacheable(cacheNames = "MapMetaDataService.mapExists")
    public boolean mapExists(@NonNull final String mapName) {
        return mapEntityPersistenceLayer.findAllMapNames().contains(mapName);
    }

}
