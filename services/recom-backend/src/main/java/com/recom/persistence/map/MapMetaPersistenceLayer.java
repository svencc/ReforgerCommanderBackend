package com.recom.persistence.map;

import com.recom.entity.map.MapMeta;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MapMetaPersistenceLayer {

    @NonNull
    private final MapMetaRepository mapMetaRepository;


    @NonNull
    public MapMeta save(@NonNull MapMeta mapMeta) {
        return mapMetaRepository.save(mapMeta);
    }

}