package com.recom.persistence.mapTopography;

import com.recom.entity.MapTopographyEntity;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface MapTopographyEntityRepository extends JpaRepository<MapTopographyEntity, Long> {

    @NonNull
    Integer countByMapName(@NonNull final String mapName);

    @NonNull
    Integer deleteByMapName(@NonNull final String mapName);

    @NonNull
    List<MapTopographyEntity> findAllByMapNameOrderByCoordinateXAscCoordinateZAsc(@NonNull final String mapName);

}