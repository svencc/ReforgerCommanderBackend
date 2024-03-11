package com.recom.persistence.map.structure;

import com.recom.entity.map.GameMap;
import com.recom.entity.map.structure.MapStructureEntity;
import com.recom.entity.map.structure.SpacialItemProjection;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Stream;

@Repository
interface MapStructureRepository extends JpaRepository<MapStructureEntity, Long> {

    @NonNull
    Integer countByGameMap(@NonNull final GameMap gameMap);

    @NonNull
    Integer deleteByGameMap(@NonNull final GameMap gameMap);

//    @NonNull
//    @Query(value = """
//            SELECT DISTINCT structure.mapName
//              FROM MapStructureEntity structure
//             ORDER BY structure.mapName ASC
//             """)
//    List<String> projectMapNames();

    @NonNull
    @Query(value = """
            SELECT DISTINCT structure.className.name
              FROM MapStructureEntity structure
             WHERE structure.gameMap = :gameMap
               AND structure.className IS NOT NULL
             ORDER BY structure.className.name ASC
             """)
    List<String> projectUtilizedClassNamesByGameMap(@NonNull final GameMap gameMap);

    @NonNull
    @Query(value = """
            SELECT DISTINCT structure.resourceName.name
              FROM MapStructureEntity structure
             WHERE structure.gameMap = :gameMap
               AND structure.resourceName IS NOT NULL
             ORDER BY structure.resourceName.name ASC
            """)
    List<String> projectUtilizedResourceNamesByGameMap(@NonNull final GameMap gameMap);

    @NonNull
    @Query(value = """
            SELECT DISTINCT structure.prefabName.name
              FROM MapStructureEntity structure
             WHERE structure.gameMap = :gameMap
               AND structure.prefabName IS NOT NULL
             ORDER BY structure.prefabName.name ASC
            """)
    List<String> projectUtilizedPrefabNameByGameMap(@NonNull final GameMap gameMap);

    @NonNull
    @Query(value = """
            SELECT DISTINCT structure.name
              FROM MapStructureEntity structure
             WHERE structure.gameMap = :gameMap
               AND structure.name IS NOT NULL
             ORDER BY structure.name ASC
            """)
    List<String> projectNamedEntitiesByGameMap(@NonNull final GameMap gameMap);

    @NonNull
    @Query(value = """
            SELECT DISTINCT structure.mapDescriptorType.name
              FROM MapStructureEntity structure
             WHERE structure.gameMap = :gameMap
               AND structure.mapDescriptorType IS NOT NULL
             ORDER BY structure.mapDescriptorType.name ASC
            """)
    List<String> projectMapMetaTypesByGameMap(@NonNull final GameMap gameMap);

    @NonNull
    List<MapStructureEntity> findAllByGameMapAndMapDescriptorTypeNameIn(
            @NonNull final GameMap gameMap,
            @NonNull final List<String> names
    );

    @NonNull
    List<MapStructureEntity> findAllByGameMapAndResourceNameNameIn(
            @NonNull final GameMap gameMap,
            @NonNull final List<String> names
    );

    @NonNull
    @Query(value = """
            SELECT structure.coordinateX as coordinateX,
                   structure.coordinateZ as coordinateY
              FROM MapStructureEntity structure
             WHERE structure.gameMap = :gameMap
               AND structure.resourceName.name IN :resourceNames
             """)
    List<SpacialItemProjection> projectAllByGameMapAndResourceNameIn(
            @NonNull final GameMap gameMap,
            @NonNull final List<String> resourceNames
    );

    @NonNull
    List<MapStructureEntity> findAllByGameMapAndPrefabNameNameIn(
            @NonNull final GameMap gameMap,
            @NonNull final List<String> names
    );

    @NonNull
    List<MapStructureEntity> findAllByGameMapAndClassNameNameIn(
            @NonNull final GameMap gameMap,
            @NonNull final List<String> names
    );

}