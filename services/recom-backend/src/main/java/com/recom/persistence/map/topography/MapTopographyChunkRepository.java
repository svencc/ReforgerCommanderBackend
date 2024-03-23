package com.recom.persistence.map.topography;

import com.recom.entity.map.GameMap;
import com.recom.entity.map.SquareKilometerTopographyChunk;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
interface MapTopographyChunkRepository extends JpaRepository<SquareKilometerTopographyChunk, Long> {

    @NonNull
    Integer countByGameMap(@NonNull final GameMap gameMap);

    @NonNull
    Integer deleteByGameMap(@NonNull final GameMap gameMap);

    @NonNull
    List<SquareKilometerTopographyChunk> findByGameMap(@NonNull final GameMap gameMap);

    @NonNull
    Optional<SquareKilometerTopographyChunk> findByGameMapAndSquareCoordinateXAndSquareCoordinateY(
            @NonNull final GameMap gameMap,
            @NonNull final Long squareCoordinateX,
            @NonNull final Long squareCoordinateY
    );

}