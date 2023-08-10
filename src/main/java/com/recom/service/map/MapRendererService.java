package com.recom.service.map;

import com.recom.dto.map.cluster.ClusterDto;
import com.recom.dto.map.renderer.MapRenderCommandDto;
import com.recom.dto.map.renderer.MapRenderCommandType;
import com.recom.dto.map.renderer.MapRenderCommandsDto;
import com.recom.dto.map.renderer.MapRendererRequestDto;
import com.recom.service.map.cluster.ClusteringService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MapRendererService {

    public static final Integer Z_INDEX_GEOMETRY = 1;
    public static final Integer COLOR_GEOMETRY = 0x0000FF;

    @NonNull
    private final ClusteringService clusteringService;

    @NonNull
    public MapRenderCommandsDto renderMap(@NonNull final MapRendererRequestDto mapRendererRequestDto) {
        final List<ClusterDto> clusterDtos = clusteringService.generateClusters(mapRendererRequestDto.getMapName());
        final List<MapRenderCommandDto> renderCommands = clusterDtos.stream()
                .map(cluster -> MapRenderCommandDto.builder()
                        .id(UUID.randomUUID())
                        .mapRenderCommandType(MapRenderCommandType.POLYGON)
                        .color(COLOR_GEOMETRY)
                        .tags(List.of("cluster"))
                        .zIndex(Z_INDEX_GEOMETRY)
                        .geometryVertices(cluster.getConvexHull().getVertices())
                        .build())
                .collect(Collectors.toList());

        final Integer zIndexMin = renderCommands.stream()
                .map(MapRenderCommandDto::getZIndex)
                .min(Integer::compare)
                .orElse(0);

        final Integer zIndexMax = renderCommands.stream()
                .map(MapRenderCommandDto::getZIndex)
                .max(Integer::compare)
                .orElse(0);

        return MapRenderCommandsDto.builder()
                .renderCommands(renderCommands)
                .zIndexMin(BigDecimal.valueOf(zIndexMin))
                .zIndexMax(BigDecimal.valueOf(zIndexMax))
                .build();
    }

}
