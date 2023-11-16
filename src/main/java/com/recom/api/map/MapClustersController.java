package com.recom.api.map;

import com.recom.api.commons.HttpCommons;
import com.recom.dto.map.cluster.ClusterResponseDto;
import com.recom.dto.map.cluster.MapClusterRequestDto;
import com.recom.entity.GameMap;
import com.recom.service.AssertionService;
import com.recom.service.ReforgerPayloadParserService;
import com.recom.service.dbcached.AsyncCacheableRequestProcessor;
import com.recom.service.map.cluster.ClusteringService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@Deprecated
@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "MapCluster")
@RequestMapping("/api/v1/map/clusters")
public class MapClustersController {

    @NonNull
    private final AssertionService assertionService;
    @NonNull
    private final ClusteringService clusteringService;
    @NonNull
    private final ReforgerPayloadParserService payloadParser;
    @NonNull
    private final AsyncCacheableRequestProcessor asyncCacheableRequestProcessor;


    @Operation(
            summary = "Determines clusters of structures",
            description = "Calculates city clusters. WIP - other cluster have to be added; db-based-config system is needed (per map).",
            security = @SecurityRequirement(name = HttpCommons.BEARER_AUTHENTICATION_REQUIREMENT)
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = HttpCommons.OK_CODE, description = HttpCommons.OK),
            @ApiResponse(responseCode = HttpCommons.UNAUTHORIZED_CODE, description = HttpCommons.UNAUTHORIZED, content = @Content())
    })
    @PostMapping(path = "/form", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<ClusterResponseDto> generateClustersForm(
            @RequestParam(required = true)
            @NonNull final Map<String, String> payload
    ) {
        log.debug("Requested POST /api/v1/clusters/form (FORM)");

        return generateClustersJSON(payloadParser.parseValidated(payload, MapClusterRequestDto.class));
    }

    @Operation(
            summary = "Determines clusters of structures",
            description = "Calculates city clusters. WIP - other cluster have to be added; db-based-config system is needed (per map).",
            security = @SecurityRequirement(name = HttpCommons.BEARER_AUTHENTICATION_REQUIREMENT)
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = HttpCommons.OK_CODE, description = HttpCommons.OK),
            @ApiResponse(responseCode = HttpCommons.UNAUTHORIZED_CODE, description = HttpCommons.UNAUTHORIZED, content = @Content())
    })
    @PostMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ClusterResponseDto> generateClustersJSON(
            @RequestBody(required = true)
            @NonNull final MapClusterRequestDto clusterRequestDto
    ) {
        log.debug("Requested POST /api/v1/clusters (JSON)");

        final GameMap gameMap = assertionService.provideMap(clusterRequestDto.getMapName());

        return asyncCacheableRequestProcessor.processRequest(
                ClusteringService.MAPENTITYPERSISTENCELAYER_GENERATECLUSTERS_CACHE,
                clusterRequestDto.getMapName(),
                () -> clusteringService.generateClusters(gameMap)
        );
    }

}