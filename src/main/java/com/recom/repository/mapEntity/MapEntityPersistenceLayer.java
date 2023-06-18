package com.recom.repository.mapEntity;

import com.recom.entity.MapEntity;
import com.recom.model.map.EnumMapDescriptorType;
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

    @Cacheable(cacheNames = "MapEntityPersistenceLayer.findAllTownBuildingEntities")
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

    @CacheEvict(cacheNames = {
            "MapEntityPersistenceLayer.findAllDistinctMapNames",
            "MapEntityPersistenceLayer.countEntitiesByMapName",
            "MapEntityPersistenceLayer.utilizedClassesByMapName"
    }, allEntries = true)
    public void evictMapMetaCaches() {
        // @TODO use this!
    }

    @Cacheable(cacheNames = "MapEntityPersistenceLayer.utilizedMapMetaTypeByMapName")
    public List<String> utilizedMapMetaTypeByMapName(@NonNull final String mapName) {
        return mapEntityRepository.projectMapMetaTypesByMapName(mapName);
    }
    
}