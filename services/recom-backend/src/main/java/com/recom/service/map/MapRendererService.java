package com.recom.service.map;

import com.recom.commons.calculator.ARGBColor;
import com.recom.dto.map.renderer.MapRenderCommandDto;
import com.recom.dto.map.renderer.MapRenderCommandType;
import com.recom.dto.map.renderer.MapRenderResponseDto;
import com.recom.entity.map.GameMap;
import com.recom.service.map.cluster.ClusteringService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MapRendererService {

    public static final String MAP_RENDERER_CACHE_NAME = "MapRendererService.mapRenderCommands";
    public static final Integer Z_INDEX_GEOMETRY = 1;
    public static final Integer COLOR_GEOMETRY = ARGBColor.ARGB(127, 0, 0, 255);

    @NonNull
    private final ClusteringService clusteringService;

    @NonNull
    public MapRenderResponseDto renderMap(@NonNull final GameMap gameMap) {
        final List<MapRenderCommandDto> renderCommands = clusteringService.generateClusters(gameMap).getClusterList().stream()
                .map(cluster -> MapRenderCommandDto.builder()
                        .id(UUID.randomUUID())
                        .mapRenderCommandType(MapRenderCommandType.POLYGON)
                        .color(COLOR_GEOMETRY)
                        .tags(List.of("cluster"))
                        .zIndex(Z_INDEX_GEOMETRY)
                        .geometryVertices(cluster.getConvexHull().getVertices())
                        .build())
                .collect(Collectors.toList());

//        final Integer zIndexMin = renderCommands.stream()
//                .com.recom.dto.map(MapRenderCommandDto::getZIndex)
//                .min(Integer::compare)
//                .orElse(0);
//
//        final Integer zIndexMax = renderCommands.stream()
//                .com.recom.dto.map(MapRenderCommandDto::getZIndex)
//                .max(Integer::compare)
//                .orElse(0);

        return MapRenderResponseDto.builder()
                .renderCommands(renderCommands)
//                .zIndexMin(BigDecimal.valueOf(zIndexMin))
//                .zIndexMax(BigDecimal.valueOf(zIndexMax))
                .build();
    }

}
