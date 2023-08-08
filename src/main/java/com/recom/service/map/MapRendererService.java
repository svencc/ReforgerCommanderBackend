package com.recom.service.map;

import com.recom.dto.map.renderer.MapRenderingsDto;
import com.recom.dto.map.renderer.MapRendererRequestDto;
import com.recom.service.map.cluster.ClusteringService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MapRendererService {

    @NonNull
    private final ClusteringService clusteringService;

    @NonNull
    public MapRenderingsDto renderMap(@NonNull final MapRendererRequestDto mapRendererRequestDto) {
        return MapRenderingsDto.builder()
                .build();
    }

}
