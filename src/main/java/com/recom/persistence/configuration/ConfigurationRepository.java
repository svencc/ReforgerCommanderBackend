package com.recom.persistence.configuration;

import com.recom.entity.Configuration;
import com.recom.entity.GameMap;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface ConfigurationRepository extends JpaRepository<Configuration, Long> {

    @NonNull
    @Query("SELECT c " +
            " FROM Configuration c " +
            "WHERE (c.gameMap = :gameMap OR c.gameMap IS NULL) " +
            "  AND c.namespace = :namespace AND c.name = :name"
    )
    List<Configuration> findAllByGameMapAndNamespaceAndName(
            @NonNull final GameMap gameMap,
            @NonNull final String namespace,
            @NonNull final String name
    );

    @NonNull
    List<Configuration> findAllByGameMapIsNull();

    @NonNull
    List<Configuration> findAllByGameMap(@NonNull final GameMap gameMap);

}