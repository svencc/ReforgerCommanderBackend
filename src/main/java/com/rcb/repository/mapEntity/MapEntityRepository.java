package com.rcb.repository.mapEntity;

import com.rcb.entity.MapEntity;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface MapEntityRepository extends JpaRepository<MapEntity, Long> {

    @NonNull
    List<MapEntity> findAllByMapNameAndMapDescriptorTypeIn(
            @NonNull final String mapName,
            @NonNull final List<String> descriptorTypes
    );

    @NonNull
    List<MapEntity> findAllByMapNameAndClassNameContaining(
            @NonNull final String mapName,
            @NonNull final String className
    );

}