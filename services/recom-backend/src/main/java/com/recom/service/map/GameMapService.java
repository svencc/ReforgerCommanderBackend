package com.recom.service.map;

import com.recom.dto.map.MapOverviewDto;
import com.recom.dto.map.create.MapCreateRequestDto;
import com.recom.dto.map.meta.MapMetaDto;
import com.recom.entity.map.GameMap;
import com.recom.entity.map.MapMeta;
import com.recom.persistence.map.GameMapPersistenceLayer;
import com.recom.persistence.map.MapMetaPersistenceLayer;
import com.recom.persistence.map.structure.MapStructurePersistenceLayer;
import com.recom.service.dbcached.DBCachedService;
import jakarta.transaction.Transactional;
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
    private final MapMetaPersistenceLayer mapMetaPersistenceLayer;
    @NonNull
    private final MapStructurePersistenceLayer mapStructurePersistenceLayer;
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
    public MapOverviewDto provideAllGameMapNames() {
        return MapOverviewDto.builder()
                .maps(gameMapPersistenceLayer.findAllMapNames())
                .build();
    }

    //    @Cacheable(cacheNames = "MapMetaDataService.mapExists") // @TODO does not reset when data are added!
    public Optional<GameMap> provideGameMap(@NonNull final String mapName) {
        return gameMapPersistenceLayer.findByName(mapName);
    }

    @NonNull
    @Transactional
    public GameMap create(@NonNull final MapCreateRequestDto mapCreateRequestDto) {
//        final GameMap createdGameMap = gameMapPersistenceLayer.save(
        final GameMap newGameMap = GameMap.builder()
                .name(mapCreateRequestDto.getMapName())
                .build();
//        );

//        mapMetaPersistenceLayer.save(
        MapMeta.builder()
                .gameMap(newGameMap)
                .dimensionX(mapCreateRequestDto.getDimensionXInMeter().longValue())
                .dimensionY(mapCreateRequestDto.getDimensionYInMeter().longValue())
                .dimensionZ(mapCreateRequestDto.getDimensionZInMeter().longValue())
                .oceanBaseHeight(mapCreateRequestDto.getOceanBaseHeight())
                .build();
//        );

        return gameMapPersistenceLayer.save(newGameMap);  // @TODO <<<<<<<<<<<<<<<<<<<<<<<<<<<< TESTEN!
//        return createdGameMap;
    }

}
