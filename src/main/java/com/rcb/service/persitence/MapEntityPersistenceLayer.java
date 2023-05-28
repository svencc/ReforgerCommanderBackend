package com.rcb.service.persitence;

import com.rcb.dto.map.scanner.EntityPackageDto;
import com.rcb.entity.MapEntity;
import com.rcb.mapper.MapEntityMapper;
import com.rcb.repository.MapEntityRepository;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MapEntityPersistenceLayer {

    private final MapEntityRepository mapEntityRepository;

    @Deprecated
    @Transactional
    public void persistMapEntityPackage(@NonNull EntityPackageDto packageDto) {
        List<MapEntity> mappedEntities = packageDto.getEntities().stream()
                .map(MapEntityMapper.INSTANCE::toEntity)
                .distinct()
                .toList();

        mapEntityRepository.saveAll(mappedEntities);
    }

}
