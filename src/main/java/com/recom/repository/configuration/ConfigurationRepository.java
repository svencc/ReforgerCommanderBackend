package com.recom.repository.configuration;

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
            "WHERE (c.mapName IN :mapNames OR c.mapName IS NULL) " +
            "  AND c.namespace = :namespace AND c.name = :name"
    )
    List<Configuration> findAllByMapNameInAndNamespaceAndName(
            @NonNull final List<String> mapNames,
            @NonNull final String namespace,
            @NonNull final String name
    );

    @NonNull
    @Query("SELECT c " +
            " FROM Configuration c " +
            "WHERE (c.mapName IN :mapNames OR c.mapName IS NULL) " +
            "  AND c.namespace = :namespace"
    )
    List<Configuration> findAllByMapNameInAndNamespace(
            @NonNull final List<String> mapNames,
            @NonNull final String namespace
    );

    @NonNull
    List<Configuration> findAllByMapNameIsNull();

}