package com.recom.service.map;

import com.recom.dto.map.meta.MapMetaDto;
import com.recom.entity.GameMap;
import com.recom.persistence.map.GameMapPersistenceLayer;
import com.recom.persistence.map.structure.MapLocatedStructurePersistenceLayer;
import com.recom.service.dbcached.DBCachedService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameMapService {

    @NonNull
    public static final String PROVIDEMAPMETALIST_PROVIDEMAPMETALIST_CACHE = "MapMetaDataService.provideMapMetaList";
    public static final String PROVIDEMAPMETALIST_PROVIDEMAPMETA_CACHE = "MapMetaDataService.provideMapMeta";

    @NonNull
    private final GameMapPersistenceLayer gameMapPersistenceLayer;
    @NonNull
    private final MapLocatedStructurePersistenceLayer mapStructurePersistenceLayer;
    @NonNull
    private final DBCachedService dbCachedService;

    @NonNull
    @Cacheable(cacheNames = PROVIDEMAPMETALIST_PROVIDEMAPMETALIST_CACHE)
    public List<MapMetaDto> provideGameMapMetaList() {
        return dbCachedService.proxyToDBCacheSafe(
                PROVIDEMAPMETALIST_PROVIDEMAPMETALIST_CACHE,
                "",
                () -> gameMapPersistenceLayer.findAllMapMeta().stream()
                        .map((@NonNull final GameMap gameMap) -> MapMetaDto.builder()
                                .mapName(gameMap.getName())
                                .entitiesCount(mapStructurePersistenceLayer.countEntitiesByMapName(gameMap))
                                .utilizedClasses(mapStructurePersistenceLayer.utilizedClassesByMapName(gameMap))
                                .utilizedResources(mapStructurePersistenceLayer.utilizedResourcesByMapName(gameMap))
                                .utilizedPrefabs(mapStructurePersistenceLayer.utilizedPrefabsByMapName(gameMap))
                                .utilizedMapMetaTypes(mapStructurePersistenceLayer.utilizedMapMetaTypeByMapName(gameMap))
                                .namedEntities(mapStructurePersistenceLayer.utilizedNamedEntitiesByMapName(gameMap))
                                .build()
                        )
                        .collect(Collectors.toCollection(ArrayList::new))

        );
    }

    @NonNull
    @Cacheable(cacheNames = PROVIDEMAPMETALIST_PROVIDEMAPMETA_CACHE)
    public MapMetaDto provideGameMapMetaData(@NonNull final GameMap gameMap) {
        log.debug("provideMapMeta({})", gameMap);
        return dbCachedService.proxyToDBCacheSafe(
                PROVIDEMAPMETALIST_PROVIDEMAPMETA_CACHE,
                gameMap.getName(),
                () -> MapMetaDto.builder()
                        .mapName(gameMap.getName())
                        .entitiesCount(mapStructurePersistenceLayer.countEntitiesByMapName(gameMap))
                        .utilizedClasses(mapStructurePersistenceLayer.utilizedClassesByMapName(gameMap))
                        .utilizedResources(mapStructurePersistenceLayer.utilizedResourcesByMapName(gameMap))
                        .utilizedPrefabs(mapStructurePersistenceLayer.utilizedPrefabsByMapName(gameMap))
                        .utilizedMapMetaTypes(mapStructurePersistenceLayer.utilizedMapMetaTypeByMapName(gameMap))
                        .namedEntities(mapStructurePersistenceLayer.utilizedNamedEntitiesByMapName(gameMap))
                        .build()

        );
    }

    @NonNull
    @Cacheable(cacheNames = "MapMetaDataService.provideAllMapNames")
    public List<String> provideAllGameMapNames() {
        return gameMapPersistenceLayer.findAllMapNames();
    }

    //    @Cacheable(cacheNames = "MapMetaDataService.mapExists") // @TODO does not reset when data are added!
    public Optional<GameMap> provideGameMap(@NonNull final String mapName) {
        return gameMapPersistenceLayer.findByName(mapName);
    }

    public void create(@NonNull final String mapName) {
        gameMapPersistenceLayer.save(
                GameMap.builder()
                        .name(mapName)
                        .build()
        );
    }
}
