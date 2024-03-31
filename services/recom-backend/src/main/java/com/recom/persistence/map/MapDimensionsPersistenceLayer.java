package com.recom.persistence.map;

import com.recom.entity.map.MapDimensions;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MapDimensionsPersistenceLayer {

    @NonNull
    private final MapDimensionsRepository mapDimensionsRepository;


    @NonNull
    public MapDimensions save(@NonNull final MapDimensions mapDimensions) {
        return mapDimensionsRepository.save(mapDimensions);
    }

}