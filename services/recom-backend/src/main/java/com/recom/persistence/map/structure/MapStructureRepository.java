package com.recom.persistence.map.structure;

import com.recom.entity.map.GameMap;
import com.recom.entity.map.structure.MapStructureEntity;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

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
            SELECT DISTINCT structure.className
              FROM MapStructureEntity structure
             WHERE structure.className IS NOT NULL
             ORDER BY structure.className.name ASC
             """)
// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< mapMeta Einschränkung
    List<String> projectUtilizedClassNamesByGameMap(@NonNull final GameMap gameMap);

    @NonNull
    @Query(value = """
            SELECT DISTINCT structure.resourceName
              FROM MapStructureEntity structure
             WHERE structure.resourceName IS NOT NULL
             ORDER BY structure.resourceName.name ASC
            """)
// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< mapMeta Einschränkung
    List<String> projectUtilizedResourceNamesByGameMap(@NonNull final GameMap gameMap);

    @NonNull
    @Query(value = """
            SELECT DISTINCT structure.prefabName
              FROM MapStructureEntity structure
             WHERE structure.prefabName IS NOT NULL
             ORDER BY structure.prefabName.name ASC
            """)
// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< mapMeta Einschränkung
    List<String> projectUtilizedPrefabNameByGameMap(@NonNull final GameMap gameMap);

    @NonNull
    @Query(value = """
            SELECT DISTINCT structure.name
              FROM MapStructureEntity structure
             WHERE structure.name IS NOT NULL
             ORDER BY structure.name ASC
            """)
// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< mapMeta Einschränkung
    List<String> projectNamedEntitiesByGameMap(@NonNull final GameMap gameMap);

    @NonNull
    @Query(value = """
            SELECT DISTINCT structure.mapDescriptorType
              FROM MapStructureEntity structure
             WHERE structure.mapDescriptorType IS NOT NULL
             ORDER BY structure.mapDescriptorType.name ASC
            """)
// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< mapMeta Einschränkung
    List<String> projectMapMetaTypesByGameMap(@NonNull final GameMap gameMap);

    @NonNull
    List<MapStructureEntity> findAllByGameMapAndMapDescriptorTypeIn(
            @NonNull final GameMap gameMap,
            @NonNull final List<String> descriptorTypes
    );

    @NonNull
    List<MapStructureEntity> findAllByGameMapAndResourceNameNameIn(
            @NonNull final GameMap gameMap,
            @NonNull final List<String> names
    );

    @NonNull
    List<MapStructureEntity> findAllByGameMapAndPrefabNameIn(
            @NonNull final GameMap gameMap,
            @NonNull final List<String> prefabNames
    );

    @NonNull
    List<MapStructureEntity> findAllByGameMapAndClassNameIn(
            @NonNull final GameMap gameMap,
            @NonNull final List<String> classNames
    );

}