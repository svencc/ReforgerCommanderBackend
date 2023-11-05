package com.recom.persistence.mapTopographyEntity;

import com.recom.entity.MapTopographyEntity;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface MapTopographyEntityRepository extends JpaRepository<MapTopographyEntity, Long> {

    @NonNull
    Integer countByMapName(@NonNull final String mapName);

    @NonNull
    Integer deleteByMapName(@NonNull final String mapName);

}