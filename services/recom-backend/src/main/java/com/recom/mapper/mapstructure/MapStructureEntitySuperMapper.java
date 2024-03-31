package com.recom.mapper.mapstructure;

import com.recom.dto.map.scanner.structure.MapStructureDto;
import com.recom.entity.map.structure.*;
import com.recom.event.listener.generic.maplocated.TransactionalMapLocatedEntityMappable;
import com.recom.persistence.map.structure.MapStructurePersistenceLayer;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class MapStructureEntitySuperMapper implements TransactionalMapLocatedEntityMappable<MapStructureEntity, MapStructureDto> {

    @NonNull
    private final MapStructurePersistenceLayer mapStructurePersistenceLayer;
    @NonNull
    private final Map<String, ClassNameEntity> newClassNameEntities = new HashMap();
    @NonNull
    private final Map<String, PrefabNameEntity> newPrefabNameEntities = new HashMap();
    @NonNull
    private final Map<String, ResourceNameEntity> newResourceNameEntities = new HashMap();
    @NonNull
    private final Map<String, MapDescriptorTypeEntity> newMapDescriptorTypeEntities = new HashMap();

    @NonNull
    private final Map<String, ClassNameEntity> indexedExistingClassNameEntities = new HashMap();
    @NonNull
    private final Map<String, PrefabNameEntity> indexedExistingPrefabNameEntities = new HashMap();
    @NonNull
    private final Map<String, ResourceNameEntity> indexedExistingResourceNameEntities = new HashMap();
    @NonNull
    private final Map<String, MapDescriptorTypeEntity> indexedMapDescriptorTypeEntities = new HashMap();


    @Override
    public void init() {
        newClassNameEntities.clear();
        newPrefabNameEntities.clear();
        indexedExistingClassNameEntities.clear();
        indexedExistingPrefabNameEntities.clear();

        prefetchRelatedEntities();
    }

    private void prefetchRelatedEntities() {
        indexedExistingClassNameEntities.putAll(
                mapStructurePersistenceLayer.findAllClassNameEntities().stream()
                        .collect(Collectors.toMap(ClassNameEntity::getName, Function.identity()))
        );

        indexedExistingPrefabNameEntities.putAll(
                mapStructurePersistenceLayer.findAllPrefabNameEntities().stream()
                        .collect(Collectors.toMap(PrefabNameEntity::getName, Function.identity()))
        );

        indexedExistingResourceNameEntities.putAll(
                mapStructurePersistenceLayer.findAllResourceNameEntities().stream()
                        .collect(Collectors.toMap(ResourceNameEntity::getName, Function.identity()))
        );

        indexedMapDescriptorTypeEntities.putAll(
                mapStructurePersistenceLayer.findAllMapDescriptorTypeEntities().stream()
                        .collect(Collectors.toMap(MapDescriptorTypeEntity::getName, Function.identity()))
        );
    }

    @NonNull
    public MapStructureEntity toEntity(@NonNull final MapStructureDto mapStructureDto) {
        final MapStructureEntity preparedStructure = MapStructureEntityMapper.INSTANCE.toEntity(mapStructureDto);

        augmentWithClassNameEntity(mapStructureDto, preparedStructure);
        augmentWithPrefabNameEntity(mapStructureDto, preparedStructure);
        augmentWithResourceNameEntity(mapStructureDto, preparedStructure);
        augmentMapDescriptorTypeEntity(mapStructureDto, preparedStructure);

        return preparedStructure;
    }

    private void augmentWithClassNameEntity(
            @NonNull final MapStructureDto structureDto,
            @NonNull final MapStructureEntity mapStructureEntity
    ) {
        if (structureDto.getClassName() == null || structureDto.getClassName().isBlank()) {
            return;
        } else if (indexedExistingClassNameEntities.containsKey(structureDto.getClassName())) {
            mapStructureEntity.setClassName(indexedExistingClassNameEntities.get(structureDto.getClassName()));
        } else if (newClassNameEntities.containsKey(structureDto.getClassName())) {
            mapStructureEntity.setClassName(newClassNameEntities.get(structureDto.getClassName()));
        } else {
            final ClassNameEntity nameEntity = ClassNameEntity.builder()
                    .name(structureDto.getClassName())
                    .build();
            newClassNameEntities.put(structureDto.getClassName(), nameEntity);
            mapStructureEntity.setClassName(nameEntity);
        }
    }

    private void augmentWithPrefabNameEntity(
            @NonNull final MapStructureDto structureDto,
            @NonNull final MapStructureEntity mapStructureEntity
    ) {
        if (structureDto.getPrefabName() == null || structureDto.getPrefabName().isBlank()) {
            return;
        } else if (indexedExistingPrefabNameEntities.containsKey(structureDto.getPrefabName())) {
            mapStructureEntity.setPrefabName(indexedExistingPrefabNameEntities.get(structureDto.getPrefabName()));
        } else if (newPrefabNameEntities.containsKey(structureDto.getPrefabName())) {
            mapStructureEntity.setPrefabName(newPrefabNameEntities.get(structureDto.getPrefabName()));
        } else {
            final PrefabNameEntity newEntity = PrefabNameEntity.builder()
                    .name(structureDto.getPrefabName())
                    .build();
            newPrefabNameEntities.put(structureDto.getPrefabName(), newEntity);
            mapStructureEntity.setPrefabName(newEntity);
        }
    }

    private void augmentWithResourceNameEntity(
            @NonNull final MapStructureDto structureDto,
            @NonNull final MapStructureEntity mapStructureEntity
    ) {
        if (structureDto.getResourceName() == null || structureDto.getResourceName().isBlank()) {
            return;
        } else if (indexedExistingResourceNameEntities.containsKey(structureDto.getResourceName())) {
            mapStructureEntity.setResourceName(indexedExistingResourceNameEntities.get(structureDto.getResourceName()));
        } else if (newResourceNameEntities.containsKey(structureDto.getResourceName())) {
            mapStructureEntity.setResourceName(newResourceNameEntities.get(structureDto.getResourceName()));
        } else {
            final ResourceNameEntity newEntity = ResourceNameEntity.builder()
                    .name(structureDto.getResourceName())
                    .build();
            newResourceNameEntities.put(structureDto.getResourceName(), newEntity);
            mapStructureEntity.setResourceName(newEntity);
        }
    }

    private void augmentMapDescriptorTypeEntity(
            @NonNull final MapStructureDto structureDto,
            @NonNull final MapStructureEntity mapStructureEntity
    ) {
        if (structureDto.getMapDescriptorType() == null || structureDto.getMapDescriptorType().isBlank()) {
            return;
        } else if (indexedMapDescriptorTypeEntities.containsKey(structureDto.getMapDescriptorType())) {
            mapStructureEntity.setMapDescriptorType(indexedMapDescriptorTypeEntities.get(structureDto.getMapDescriptorType()));
        } else if (newMapDescriptorTypeEntities.containsKey(structureDto.getMapDescriptorType())) {
            mapStructureEntity.setMapDescriptorType(newMapDescriptorTypeEntities.get(structureDto.getMapDescriptorType()));
        } else {
            final MapDescriptorTypeEntity newEntity = MapDescriptorTypeEntity.builder()
                    .name(structureDto.getMapDescriptorType())
                    .build();
            newMapDescriptorTypeEntities.put(structureDto.getMapDescriptorType(), newEntity);
            mapStructureEntity.setMapDescriptorType(newEntity);
        }
    }

}