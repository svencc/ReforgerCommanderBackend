package com.recom.service.map;

import com.recom.dto.map.meta.MapMetaDto;
import com.recom.persistence.mapEntity.MapEntityPersistenceLayer;
import com.recom.service.dbcached.DBCachedService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MapMetaDataService {

    @NonNull
    public static final String PROVIDEMAPMETALIST_PROVIDEMAPMETALIST_CACHE = "MapMetaDataService.provideMapMetaList";
    public static final String PROVIDEMAPMETALIST_PROVIDEMAPMETA_CACHE = "MapMetaDataService.provideMapMeta";

    @NonNull
    private final MapEntityPersistenceLayer mapEntityPersistenceLayer;
    @NonNull
    private final DBCachedService dbCachedService;

    @NonNull
    @Cacheable(cacheNames = PROVIDEMAPMETALIST_PROVIDEMAPMETALIST_CACHE)
    public List<MapMetaDto> provideMapMetaList() {
        return dbCachedService.proxyToDBCacheSafe(
                PROVIDEMAPMETALIST_PROVIDEMAPMETALIST_CACHE,
                "",
                () -> mapEntityPersistenceLayer.findAllMapNames().stream()
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
                        .collect(Collectors.toCollection(ArrayList::new))

        );
    }

    @NonNull
    @Cacheable(cacheNames = PROVIDEMAPMETALIST_PROVIDEMAPMETA_CACHE)
    public MapMetaDto provideMapMeta(@NonNull final String mapName) {
        log.debug("provideMapMeta({})", mapName);
        return dbCachedService.proxyToDBCacheSafe(
                PROVIDEMAPMETALIST_PROVIDEMAPMETA_CACHE,
                mapName,
                () -> MapMetaDto.builder()
                        .mapName(mapName)
                        .entitiesCount(mapEntityPersistenceLayer.countEntitiesByMapName(mapName))
                        .utilizedClasses(mapEntityPersistenceLayer.utilizedClassesByMapName(mapName))
                        .utilizedResources(mapEntityPersistenceLayer.utilizedResourcesByMapName(mapName))
                        .utilizedPrefabs(mapEntityPersistenceLayer.utilizedPrefabsByMapName(mapName))
                        .utilizedMapMetaTypes(mapEntityPersistenceLayer.utilizedMapMetaTypeByMapName(mapName))
                        .namedEntities(mapEntityPersistenceLayer.utilizedNamedEntitiesByMapName(mapName))
                        .build()

        );
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
