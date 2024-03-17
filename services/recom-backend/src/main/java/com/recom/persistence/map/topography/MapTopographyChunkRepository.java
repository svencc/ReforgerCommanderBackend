package com.recom.persistence.map.topography;

import com.recom.entity.map.GameMap;
import com.recom.entity.map.SquareKilometerTopographyChunk;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface MapTopographyChunkRepository extends JpaRepository<SquareKilometerTopographyChunk, Long> {

    @NonNull
    Integer countByGameMap(@NonNull final GameMap gameMap);

    @NonNull
    Integer deleteByGameMap(@NonNull final GameMap gameMap);

    @NonNull
    List<SquareKilometerTopographyChunk> findByGameMap(@NonNull final GameMap gameMap);

}