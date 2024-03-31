package com.recom.persistence.map.chunk.structure;

import com.recom.entity.map.GameMap;
import com.recom.entity.map.SquareKilometerStructureChunk;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
interface MapStructureChunkRepository extends JpaRepository<SquareKilometerStructureChunk, Long> {

    @NonNull
    Integer countByGameMap(@NonNull final GameMap gameMap);

    @NonNull
    Integer deleteByGameMap(@NonNull final GameMap gameMap);

    @NonNull
    List<SquareKilometerStructureChunk> findByGameMap(@NonNull final GameMap gameMap);

    @NonNull
    Optional<SquareKilometerStructureChunk> findByGameMapAndSquareCoordinateXAndSquareCoordinateY(
            @NonNull final GameMap gameMap,
            @NonNull final Long squareCoordinateX,
            @NonNull final Long squareCoordinateY
    );

}