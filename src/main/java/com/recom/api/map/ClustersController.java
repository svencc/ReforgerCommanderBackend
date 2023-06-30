package com.recom.api.map;

import com.recom.api.commons.HttpCommons;
import com.recom.dto.map.cluster.ClusterListDto;
import com.recom.dto.map.cluster.MapClusterRequestDto;
import com.recom.service.ReforgerPayloadParserService;
import com.recom.service.AssertionService;
import com.recom.service.map.MapMetaDataService;
import com.recom.service.map.cluster.ClusteringService;
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
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "MapCluster")
@RequestMapping("/api/v1/map/clusters")
public class ClustersController {

    @NonNull
    private final AssertionService assertionService;
    @NonNull
    private final MapMetaDataService mapMetaDataService;
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
    public ResponseEntity<ClusterListDto> generateClustersForm(
            @RequestParam(required = true)
            @NonNull final Map<String, String> payload
    ) {
        log.debug("Requested POST /api/v1/clusters (FORM)");

        return generateClustersJSON(payloadParser.parseValidated(payload, MapClusterRequestDto.class));
    }

    @Operation(
            summary = "Determines clusters of Town/City/Village and military relevant targets.",
            description = "Calculates city clusters. WIP - other cluster have to be added; db-based-config system is needed (per map)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = HttpCommons.OK_CODE, description = HttpCommons.OK)
    })
    @PostMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ClusterListDto> generateClustersJSON(
            @RequestBody(required = true)
            @NonNull final MapClusterRequestDto clusterRequestDto
    ) {
        log.debug("Requested POST /api/v1/clusters (JSON)");

        assertionService.assertMapExists(clusterRequestDto.getMapName());

        return ResponseEntity.status(HttpStatus.OK)
                .cacheControl(CacheControl.noCache())
                .body(ClusterListDto.builder()
                        .clusterList(clusteringService.generateClusters(clusterRequestDto.getMapName()))
                        .build()
                );
    }

}
