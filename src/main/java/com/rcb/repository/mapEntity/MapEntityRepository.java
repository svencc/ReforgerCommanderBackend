package com.rcb.repository.mapEntity;

import com.rcb.entity.MapEntity;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface MapEntityRepository extends JpaRepository<MapEntity, Long> {

    @NonNull
    Integer countByMapName(
            @NonNull final String mapName
    );

    @NonNull
    @Query(value = "SELECT DISTINCT me.mapName FROM MapEntity me ")
    List<String> projectMapNames();

    @NonNull
    @Query(value = "SELECT DISTINCT me.className FROM MapEntity me WHERE me.className IS NOT NULL")
    List<String> projectUtilizedClassNamesByMapName(
            @NonNull final String mapName
    );

    @NonNull
    @Query(value = "SELECT DISTINCT me.resourceName FROM MapEntity me WHERE me.resourceName IS NOT NULL")
    List<String> projectUtilizedResourceNamesByMapName(
            @NonNull final String mapName
    );

    @NonNull
    @Query(value = "SELECT DISTINCT me.prefabName FROM MapEntity me WHERE me.prefabName IS NOT NULL")
    List<String> projectUtilizedPrefabNameByMapName(
            @NonNull final String mapName
    );

    @NonNull
    @Query(value = "SELECT DISTINCT me.name FROM MapEntity me WHERE me.name IS NOT NULL")
    List<String> projectNamedEntitiesByMapName(
            @NonNull final String mapName
    );

    @NonNull
    List<MapEntity> findAllByMapNameAndMapDescriptorTypeIn(
            @NonNull final String mapName,
            @NonNull final List<String> descriptorTypes
    );

    @NonNull
    List<MapEntity> findAllByMapNameAndClassNameContaining(
            @NonNull final String mapName,
            @NonNull final String className
    );

}