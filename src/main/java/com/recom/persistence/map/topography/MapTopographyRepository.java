package com.recom.persistence.map.topography;

import com.recom.entity.GameMap;
import com.recom.entity.MapTopography;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface MapTopographyRepository extends JpaRepository<MapTopography, Long> {

    @NonNull
    Integer countByGameMap(@NonNull final GameMap gameMap);

    @NonNull
    Integer deleteByGameMap(@NonNull final GameMap gameMap);

}