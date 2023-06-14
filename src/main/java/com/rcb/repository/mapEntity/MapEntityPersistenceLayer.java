package com.rcb.repository.mapEntity;

import com.rcb.entity.MapEntity;
import com.rcb.model.EnumMapDescriptorType;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MapEntityPersistenceLayer {

    @NonNull
    private final MapEntityRepository mapEntityRepository;

    @Transactional(readOnly = false)
    public List<MapEntity> saveAll(List<MapEntity> distinctEntities) {
        return mapEntityRepository.saveAll(distinctEntities);
    }

    @Transactional(readOnly = false)
    public Integer deleteMapEntities(@NonNull final String mapName) {
        return mapEntityRepository.deleteByMapName(mapName);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "MapEntityPersistenceLayer.findAllTownBuildingEntities", key = "#mapName")
    public List<MapEntity> findAllTownBuildingEntities(@NonNull final String mapName) {
        return mapEntityRepository.findAllByMapNameAndClassNameContaining(mapName, "building");
//        return mapEntityRepository.findAllByMapNameAndMapDescriptorTypeIn(mapName, Stream.of(
//                        EnumMapDescriptorType.MDT_HOUSE,
//                        EnumMapDescriptorType.MDT_BUILDING,
//                        EnumMapDescriptorType.MDT_AIRPORT,
//                        EnumMapDescriptorType.MDT_FIREDEP,
//                        EnumMapDescriptorType.MDT_HOSPITAL,
//                        EnumMapDescriptorType.MDT_HOTEL,
//                        EnumMapDescriptorType.MDT_FUELSTATION,
//                        EnumMapDescriptorType.MDT_POLICE,
//                        EnumMapDescriptorType.MDT_PORT,
//                        EnumMapDescriptorType.MDT_PUB,
//                        EnumMapDescriptorType.MDT_STORE
//                )
//                .map(Enum::name)
//                .toList());
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "MapEntityPersistenceLayer.findAllTownEntities", key = "#mapName")
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

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "MapEntityPersistenceLayer.findAllMapNames")
    public List<String> findAllMapNames() {
        return mapEntityRepository.projectMapNames();
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "MapEntityPersistenceLayer.utilizedClassesByMapName", key = "#mapName")
    public List<String> utilizedClassesByMapName(@NonNull final String mapName) {
        return mapEntityRepository.projectUtilizedClassNamesByMapName(mapName);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "MapEntityPersistenceLayer.utilizedResourcesByMapName", key = "#mapName")
    public List<String> utilizedResourcesByMapName(@NonNull final String mapName) {
        return mapEntityRepository.projectUtilizedResourceNamesByMapName(mapName);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "MapEntityPersistenceLayer.utilizedPrefabsByMapName", key = "#mapName")
    public List<String> utilizedPrefabsByMapName(@NonNull final String mapName) {
        return mapEntityRepository.projectUtilizedPrefabNameByMapName(mapName);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "MapEntityPersistenceLayer.utilizedNamedEntitiesByMapName", key = "#mapName")
    public List<String> utilizedNamedEntitiesByMapName(@NonNull final String mapName) {
        return mapEntityRepository.projectNamedEntitiesByMapName(mapName);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "MapEntityPersistenceLayer.countEntitiesByMapName", key = "#mapName")
    public Integer countEntitiesByMapName(@NonNull final String mapName) {
        return mapEntityRepository.countByMapName(mapName);
    }

    @CacheEvict(cacheNames = {
            "MapEntityPersistenceLayer.findAllDistinctMapNames",
            "MapEntityPersistenceLayer.countEntitiesByMapName",
            "MapEntityPersistenceLayer.utilizedClassesByMapName"
    }, allEntries = true)
    public void evictMapMetaCaches() {
        // @TODO use this!
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "MapEntityPersistenceLayer.utilizedMapMetaTypeByMapName", key = "#mapName")
    public List<String> utilizedMapMetaTypeByMapName(@NonNull final String mapName) {
        return mapEntityRepository.projectMapMetaTypesByMapName(mapName);
    }
}