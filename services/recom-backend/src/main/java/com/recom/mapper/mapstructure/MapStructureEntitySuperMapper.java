package com.recom.mapper.mapstructure;

import com.recom.dto.map.scanner.structure.MapStructureEntityDto;
import com.recom.entity.map.structure.ClassNameEntity;
import com.recom.entity.map.structure.MapStructureEntity;
import com.recom.entity.map.structure.PrefabNameEntity;
import com.recom.event.listener.generic.maplocated.TransactionalMapLocatedEntityMappable;
import com.recom.persistence.map.structure.MapStructurePersistenceLayer;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class MapStructureEntitySuperMapper implements TransactionalMapLocatedEntityMappable<MapStructureEntity, MapStructureEntityDto> {

    @NonNull
    private final MapStructurePersistenceLayer mapStructurePersistenceLayer;
    @NonNull
    private final Map<String, ClassNameEntity> newClassNameEntities = new HashMap();
    @NonNull
    private final Map<String, PrefabNameEntity> newPrefabNameEntities = new HashMap();

    @NonNull
    private final Map<String, ClassNameEntity> indexedExistingClassNameEntities = new HashMap();
    @NonNull
    private final Map<String, PrefabNameEntity> indexedExistingPrefabNameEntities = new HashMap();


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
    }

    @NonNull
    public MapStructureEntity toEntity(@NonNull final MapStructureEntityDto mapStructureEntityDto) {
        final MapStructureEntity preparedStructure = MapStructureEntityMapper.INSTANCE.toEntity(mapStructureEntityDto);

        augmentWithClassNameEntity(mapStructureEntityDto, preparedStructure);
        augmentWithPrefabNameEntity(mapStructureEntityDto, preparedStructure);

        return preparedStructure;
    }

    private void augmentWithClassNameEntity(
            @NonNull final MapStructureEntityDto structureDto,
            @NonNull final MapStructureEntity mapStructureEntity
    ) {
        if (indexedExistingClassNameEntities.containsKey(structureDto.getClassName())) {
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
            @NonNull final MapStructureEntityDto structureDto,
            @NonNull final MapStructureEntity mapStructureEntity
    ) {
        if (indexedExistingPrefabNameEntities.containsKey(structureDto.getPrefabName())) {
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

}