package com.rcb.repository;

import com.rcb.entity.MapEntity;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MapEntityPersistenceLayer {

    @NonNull
    private final MapEntityRepository mapEntityRepository;

    @Transactional
    public List<MapEntity> saveAll(List<MapEntity> distinctEntities) {
        return mapEntityRepository.saveAll(distinctEntities);
    }

}