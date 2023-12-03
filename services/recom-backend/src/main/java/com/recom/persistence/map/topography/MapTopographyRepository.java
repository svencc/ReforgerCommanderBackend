package com.recom.persistence.map.topography;

import com.recom.entity.map.GameMap;
import com.recom.entity.map.MapTopography;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
interface MapTopographyRepository extends JpaRepository<MapTopography, Long> {

    @NonNull
    Integer countByGameMap(@NonNull final GameMap gameMap);

    @NonNull
    Integer deleteByGameMap(@NonNull final GameMap gameMap);

    @NonNull
    Optional<MapTopography> findByGameMap(@NonNull final GameMap gameMap);

}