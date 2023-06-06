package com.rcb.repository.mapEntity;

import com.rcb.entity.MapEntity;
import com.rcb.model.EnumMapDescriptorType;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
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

}