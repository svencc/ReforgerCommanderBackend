package com.recom.persistence.configuration;

import com.recom.entity.Configuration;
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
            "WHERE (c.mapName = :mapName OR c.mapName IS NULL) " +
            "  AND c.namespace = :namespace AND c.name = :name"
    )
    List<Configuration> findAllByMapNameAndNamespaceAndName(
            @NonNull final String mapName,
            @NonNull final String namespace,
            @NonNull final String name
    );

    @NonNull
    List<Configuration> findAllByMapNameIsNull();

    @NonNull
    List<Configuration> findAllByMapName(@NonNull final String mapName);

}