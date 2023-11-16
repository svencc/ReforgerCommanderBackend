package com.recom.persistence.map;

import com.recom.entity.GameMap;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
interface GameMapRepository extends JpaRepository<GameMap, Long> {

    @NonNull
    Integer deleteByName(@NonNull final String name);

    Optional<GameMap> findByName(@NonNull final String name);
}