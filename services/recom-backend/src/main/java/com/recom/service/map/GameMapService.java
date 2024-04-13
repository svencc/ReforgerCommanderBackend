package com.recom.service.map;

import com.recom.dto.map.MapOverviewDto;
import com.recom.dto.map.create.MapCreateRequestDto;
import com.recom.dto.map.meta.MapMetaDto;
import com.recom.entity.map.*;
import com.recom.persistence.map.GameMapPersistenceLayer;
import com.recom.persistence.map.structure.MapStructurePersistenceLayer;
import com.recom.service.dbcached.DBCachedService;
import com.recom.service.messagebus.chunkscanrequest.MapStructureChunkScanRequestNotificationService;
import com.recom.service.messagebus.chunkscanrequest.MapTopographyChunkScanRequestNotificationService;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final MapStructurePersistenceLayer mapStructurePersistenceLayer;
    @NonNull
    private final MapTopographyChunkScanRequestNotificationService mapTopographyChunkScanRequestNotificationService;
    @NonNull
    private final MapStructureChunkScanRequestNotificationService mapStructureChunkScanRequestNotificationService;
    @NonNull
    private final DBCachedService dbCachedService;


    @NonNull
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
    public MapOverviewDto provideAllGameMapNames() {
        return MapOverviewDto.builder()
                .maps(gameMapPersistenceLayer.findAllMapNames())
                .build();
    }

    @NonNull
    public Optional<GameMap> provideGameMap(@NonNull final String mapName) {
        return gameMapPersistenceLayer.findByName(mapName);
    }

    @NonNull
    @Transactional
    public GameMap create(@NonNull final MapCreateRequestDto mapCreateRequestDto) {
        final GameMap newGameMap = GameMap.builder()
                .name(mapCreateRequestDto.getMapName())
                .build();

        final MapDimensions newMapDimensions = MapDimensions.builder()
                .gameMap(newGameMap)
                .dimensionX(mapCreateRequestDto.getDimensionXInMeter().longValue())
                .dimensionY(mapCreateRequestDto.getDimensionYInMeter().longValue())
                .dimensionZ(mapCreateRequestDto.getDimensionZInMeter().longValue())
                .oceanBaseHeight(mapCreateRequestDto.getOceanBaseHeight())
                .build();
        newGameMap.setMapDimensions(newMapDimensions);

        generatePreparedChunks(newMapDimensions, newGameMap);

        final GameMap persistedGameMap = gameMapPersistenceLayer.save(newGameMap);

//        mapTopographyChunkScanRequestNotificationService.requestMapTopographyChunkScan(persistedGameMap);
//        mapStructureChunkScanRequestNotificationService.requestMapStructureChunkScan(persistedGameMap);

        return persistedGameMap;
    }

    private void generatePreparedChunks(
            @NonNull final MapDimensions newMapDimensions,
            @NonNull final GameMap newGameMap
    ) {
        final int chunksInXDimension = (int) Math.ceil(newMapDimensions.getDimensionX() / 1000f);
        final int chunksInZDimension = (int) Math.ceil(newMapDimensions.getDimensionZ() / 1000f);

        final ArrayList<SquareKilometerTopographyChunk> topographyChunks = new ArrayList<>();
        final ArrayList<SquareKilometerStructureChunk> structureChunks = new ArrayList<>();
        for (long x = 0; x < chunksInXDimension; x++) {
            for (long z = 0; z < chunksInZDimension; z++) {
                topographyChunks.add(SquareKilometerTopographyChunk.builder()
                        .gameMap(newGameMap)
                        .squareCoordinateX(x)
                        .squareCoordinateY(z)
                        .status(ChunkStatus.OPEN)
                        .build()
                );
                structureChunks.add(SquareKilometerStructureChunk.builder()
                        .gameMap(newGameMap)
                        .squareCoordinateX(x)
                        .squareCoordinateY(z)
                        .status(ChunkStatus.OPEN)
                        .build()
                );
            }
        }

        newGameMap.getTopographyChunks().addAll(topographyChunks);
        newGameMap.getStructureChunks().addAll(structureChunks);
    }

}
