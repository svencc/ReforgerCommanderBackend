package com.recom.persistence.map.structure;

import com.recom.commons.model.maprendererpipeline.dataprovider.SpacialItem;
import com.recom.entity.map.GameMap;
import com.recom.entity.map.structure.*;
import com.recom.event.listener.generic.generic.MapLocatedEntityPersistable;
import com.recom.model.map.EnumMapDescriptorType;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MapStructurePersistenceLayer implements MapLocatedEntityPersistable<MapStructureEntity> {

    @NonNull
    private final MapStructureRepository mapStructureRepository;
    @NonNull
    private final ClassNameRepository classNameRepository;
    @NonNull
    private final PrefabNameRepository prefabNameRepository;
    @NonNull
    private final ResourceNameRepository resourceNameRepository;
    @NonNull
    private final MapDescriptorTypeRepository mapDescriptorTypeRepository;

    @NonNull
    public List<MapStructureEntity> saveAll(@NonNull List<MapStructureEntity> distinctEntities) {
        return mapStructureRepository.saveAll(distinctEntities);
    }

    public Integer deleteMapEntities(@NonNull final GameMap gameMap) {
        return mapStructureRepository.deleteByGameMap(gameMap);
    }

    public List<MapStructureEntity> findAllByMapNameAndResourceNameIn(
            @NonNull final GameMap gameMap,
            @NonNull final List<String> resourceNames
    ) {
        List<MapStructureEntity> allByGameMapAndResourceNameIn = mapStructureRepository.findAllByGameMapAndResourceNameNameIn(gameMap, resourceNames);
        return allByGameMapAndResourceNameIn;
    }

    @NonNull
    @SuppressWarnings("unchecked")
    public <T extends SpacialItem> List<T> projectStructureItemByMapNameAndResourceNameIn(
            @NonNull final GameMap gameMap,
            @NonNull final List<String> resourceNames
    ) {
        return mapStructureRepository.projectAllByGameMapAndResourceNameIn(gameMap, resourceNames).stream()
                .map(entity -> (T) entity)
                .toList();
    }

    @NonNull
    public List<MapStructureEntity> findAllTownEntities(@NonNull final GameMap gameMap) {
        return mapStructureRepository.findAllByGameMapAndMapDescriptorTypeNameIn(gameMap, Stream.of(
                        EnumMapDescriptorType.MDT_NAME_SETTLEMENT,
                        EnumMapDescriptorType.MDT_NAME_CITY,
                        EnumMapDescriptorType.MDT_NAME_TOWN,
                        EnumMapDescriptorType.MDT_NAME_VILLAGE
                )
                .map(Enum::name)
                .toList());
    }

//    public List<String> findAllMapNames() {
//        return mapStructureRepository.projectMapNames();
//    }

    @NonNull
    public List<String> utilizedClassesByMapName(@NonNull final GameMap gameMap) {
        return mapStructureRepository.projectUtilizedClassNamesByGameMap(gameMap);
    }

    @NonNull
    public List<String> utilizedResourcesByMapName(@NonNull final GameMap gameMap) {
        return mapStructureRepository.projectUtilizedResourceNamesByGameMap(gameMap);
    }

    @NonNull
    public List<String> utilizedPrefabsByMapName(@NonNull final GameMap gameMap) {
        return mapStructureRepository.projectUtilizedPrefabNameByGameMap(gameMap);
    }

    @NonNull
    public List<String> utilizedNamedEntitiesByMapName(@NonNull final GameMap gameMap) {
        return mapStructureRepository.projectNamedEntitiesByGameMap(gameMap);
    }

    @NonNull
    public Integer countEntitiesByMapName(@NonNull final GameMap gameMap) {
        return mapStructureRepository.countByGameMap(gameMap);
    }

    @NonNull
    public List<String> utilizedMapMetaTypeByMapName(@NonNull final GameMap gameMap) {
        return mapStructureRepository.projectMapMetaTypesByGameMap(gameMap);
    }

    @NonNull
    public List<MapStructureEntity> findAllByPrefabIn(
            @NonNull final GameMap gameMap,
            @NonNull final List<String> prefabNames
    ) {
        return mapStructureRepository.findAllByGameMapAndPrefabNameNameIn(gameMap, prefabNames);
    }

    @NonNull
    public List<MapStructureEntity> findAllByClassIn(
            @NonNull final GameMap gameMap,
            @NonNull final List<String> classNames
    ) {
        return mapStructureRepository.findAllByGameMapAndClassNameNameIn(gameMap, classNames);
    }

    @NonNull
    public List<MapStructureEntity> findAllByMapDescriptorTypeIn(
            @NonNull final GameMap gameMap,
            @NonNull final List<String> descriptorTypes
    ) {
        return mapStructureRepository.findAllByGameMapAndMapDescriptorTypeNameIn(gameMap, descriptorTypes);
    }

    @NonNull
    public List<ClassNameEntity> findAllClassNameEntities() {
        return classNameRepository.findAll();
    }

    @NonNull
    public List<PrefabNameEntity> findAllPrefabNameEntities() {
        return prefabNameRepository.findAll();
    }

    @NonNull
    public List<ResourceNameEntity> findAllResourceNameEntities() {
        return resourceNameRepository.findAll();
    }

    @NonNull
    public List<MapDescriptorTypeEntity> findAllMapDescriptorTypeEntities() {
        return mapDescriptorTypeRepository.findAll();
    }

}