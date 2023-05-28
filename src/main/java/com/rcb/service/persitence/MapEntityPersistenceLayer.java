package com.rcb.service.persitence;

import com.rcb.dto.mapScanner.MapScannerEntityPackageDto;
import com.rcb.mapper.MapEntityMapper;
import com.rcb.repository.MapEntityRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MapEntityPersistenceLayer {

    private final MapEntityRepository mapEntityRepository;

    @Deprecated
    public void persistMapEntityPackage(@NonNull MapScannerEntityPackageDto packageDto) {
        packageDto.getEntities().stream()
                .map(MapEntityMapper.INSTANCE::toDto)
                .toList();

        mapEntityRepository.saveAll()
    }

}
