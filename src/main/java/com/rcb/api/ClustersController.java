package com.rcb.api;

import com.rcb.api.commons.HttpCommons;
import com.rcb.dto.map.cluster.ClusterListDto;
import com.rcb.dto.map.cluster.MapClusterRequestDto;
import com.rcb.service.ReforgerPayloadParserService;
import com.rcb.service.map.cluster.ClusteringService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Clustering")
@RequestMapping("/api/v1/clusters")
public class ClustersController {

    @NonNull
    private final ClusteringService clusteringService;
    @NonNull
    private final ReforgerPayloadParserService payloadParser;

    @Operation(
            summary = "Determines clusters of Town/City/Village and military relevant targets.",
            description = "Calculates city clusters. WIP - other cluster have to be added; db-based-config system is needed (per map)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = HttpCommons.OK_CODE, description = HttpCommons.OK)
    })
    @PostMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<ClusterListDto> generateClusters(
            @RequestParam(required = true)
            @NonNull final Map<String, String> payload
    ) {
        log.debug("Requested POST /api/v1/clusters");

        final MapClusterRequestDto mapClusterRequestDto = payloadParser.parseValidated(payload, MapClusterRequestDto.class);

        return ResponseEntity.status(HttpStatus.OK)
                .cacheControl(CacheControl.noCache())
                .body(ClusterListDto.builder()
                        .clusterList(
                                clusteringService.generateClusters(
                                        mapClusterRequestDto.getMapName(),
                                        mapClusterRequestDto.getMaximumRadiusOfTheNeighborhoodEpsilon().doubleValue(),
                                        mapClusterRequestDto.getMinimumNumberOfPointsNeededForCluster()
                                )
                        )
                        .build()
                );
    }

}
