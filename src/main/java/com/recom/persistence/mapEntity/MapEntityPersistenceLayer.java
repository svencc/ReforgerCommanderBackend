package com.recom.persistence.mapEntity;

import com.recom.entity.MapEntity;
import com.recom.model.map.EnumMapDescriptorType;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MapEntityPersistenceLayer {

    @NonNull
    private final MapEntityRepository mapEntityRepository;

    public List<MapEntity> saveAll(List<MapEntity> distinctEntities) {
        return mapEntityRepository.saveAll(distinctEntities);
    }

    public Integer deleteMapEntities(@NonNull final String mapName) {
        return mapEntityRepository.deleteByMapName(mapName);
    }

    @Cacheable(cacheNames = "MapEntityPersistenceLayer.findAllByMapNameAndResourceNameIn")
    public List<MapEntity> findAllByMapNameAndResourceNameIn(
            @NonNull final String mapName,
            @NonNull final List<String> resourceNames
    ) {
        return mapEntityRepository.findAllByMapNameAndResourceNameIn(mapName, resourceNames);
    }

    @Cacheable(cacheNames = "MapEntityPersistenceLayer.findAllTownEntities")
    public List<MapEntity> findAllTownEntities(@NonNull final String mapName) {
        return mapEntityRepository.findAllByMapNameAndMapDescriptorTypeIn(mapName, Stream.of(
                        EnumMapDescriptorType.MDT_NAME_SETTLEMENT,
                        EnumMapDescriptorType.MDT_NAME_CITY,
                        EnumMapDescriptorType.MDT_NAME_TOWN,
                        EnumMapDescriptorType.MDT_NAME_VILLAGE
                )
                .map(Enum::name)
                .toList());
    }

    @Cacheable(cacheNames = "MapEntityPersistenceLayer.findAllMapNames")
    public List<String> findAllMapNames() {
        return mapEntityRepository.projectMapNames();
    }

    @Cacheable(cacheNames = "MapEntityPersistenceLayer.utilizedClassesByMapName")
    public List<String> utilizedClassesByMapName(@NonNull final String mapName) {
        return mapEntityRepository.projectUtilizedClassNamesByMapName(mapName);
    }

    @Cacheable(cacheNames = "MapEntityPersistenceLayer.utilizedResourcesByMapName")
    public List<String> utilizedResourcesByMapName(@NonNull final String mapName) {
        return mapEntityRepository.projectUtilizedResourceNamesByMapName(mapName);
    }

    @Cacheable(cacheNames = "MapEntityPersistenceLayer.utilizedPrefabsByMapName")
    public List<String> utilizedPrefabsByMapName(@NonNull final String mapName) {
        return mapEntityRepository.projectUtilizedPrefabNameByMapName(mapName);
    }

    @Cacheable(cacheNames = "MapEntityPersistenceLayer.utilizedNamedEntitiesByMapName")
    public List<String> utilizedNamedEntitiesByMapName(@NonNull final String mapName) {
        return mapEntityRepository.projectNamedEntitiesByMapName(mapName);
    }

    @Cacheable(cacheNames = "MapEntityPersistenceLayer.countEntitiesByMapName")
    public Integer countEntitiesByMapName(@NonNull final String mapName) {
        return mapEntityRepository.countByMapName(mapName);
    }

    @Cacheable(cacheNames = "MapEntityPersistenceLayer.utilizedMapMetaTypeByMapName")
    public List<String> utilizedMapMetaTypeByMapName(@NonNull final String mapName) {
        return mapEntityRepository.projectMapMetaTypesByMapName(mapName);
    }

    @NonNull
    @Cacheable(cacheNames = "MapEntityPersistenceLayer.findAllByMapNameAndPrefabNameIn")
    public List<MapEntity> findAllByPrefabIn(
            @NonNull final String mapName,
            @NonNull final List<String> prefabNames
    ) {
        return mapEntityRepository.findAllByMapNameAndPrefabNameIn(mapName, prefabNames);
    }

    @NonNull
    @Cacheable(cacheNames = "MapEntityPersistenceLayer.findAllByClassIn")
    public List<MapEntity> findAllByClassIn(
            @NonNull final String mapName,
            @NonNull final List<String> classNames
    ) {
        return mapEntityRepository.findAllByMapNameAndClassNameIn(mapName, classNames);
    }

    @NonNull
    @Cacheable(cacheNames = "MapEntityPersistenceLayer.findAllByMapDescriptorTypeIn")
    public List<MapEntity> findAllByMapDescriptorTypeIn(
            @NonNull final String mapName,
            @NonNull final List<String> descriptorTypes
    ) {
        return mapEntityRepository.findAllByMapNameAndMapDescriptorTypeIn(mapName, descriptorTypes);
    }

}