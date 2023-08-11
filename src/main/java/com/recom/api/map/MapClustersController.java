package com.recom.api.map;

import com.recom.api.commons.HttpCommons;
import com.recom.dto.map.cluster.ClusterDto;
import com.recom.dto.map.cluster.ClusterListDto;
import com.recom.dto.map.cluster.MapClusterRequestDto;
import com.recom.exception.DBCachedDeserializationException;
import com.recom.service.*;
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
import org.springframework.cache.CacheManager;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;


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
    private final MutexService mutexService;
    @NonNull
    private final CacheManager cacheManager;
    @NonNull
    private final DBCachedManager dbCachedManager;
    @NonNull
    private final ExecutorProvider executorProvider;


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
    public ResponseEntity<ClusterListDto> generateClustersForm(
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
    public ResponseEntity<ClusterListDto> generateClustersJSON(
            @RequestBody(required = true)
            @NonNull final MapClusterRequestDto clusterRequestDto
    ) {
        log.debug("Requested POST /api/v1/clusters (JSON)");

        assertionService.assertMapExists(clusterRequestDto.getMapName());

        if (dbCachedManager.isCached(ClusteringService.MAPENTITYPERSISTENCELAYER_GENERATECLUSTERS_CACHE, clusterRequestDto.getMapName())) {
            try {
                // if present in cache, return it
                final Optional<ArrayList<ClusterDto>> clusterList = dbCachedManager.get(ClusteringService.MAPENTITYPERSISTENCELAYER_GENERATECLUSTERS_CACHE, clusterRequestDto.getMapName());
                return clusterList.map((value) -> ResponseEntity.status(HttpStatus.OK)
                                .cacheControl(CacheControl.noCache())
                                .body(ClusterListDto.builder().clusterList(value).build())
                        )
                        .orElseGet(() -> ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .cacheControl(CacheControl.noCache())
                                .build()
                        );
            } catch (DBCachedDeserializationException dbcde) {
                log.error("Unable to deserialize cached value; Generating new one");
                // if present in cache, but deserialization failed, delete it and generate new one
                dbCachedManager.delete(ClusteringService.MAPENTITYPERSISTENCELAYER_GENERATECLUSTERS_CACHE, clusterRequestDto.getMapName());
            }
        }

        // if not present in cache, generate it
        final String mutexFormat = "ClustersController.generateClustersJSON#%1s";

        boolean claimed = mutexService.claim(String.format(mutexFormat, clusterRequestDto.getMapName()));
        if (claimed) {
            log.info("Generating clusters for map {}.", clusterRequestDto.getMapName());

            CompletableFuture.supplyAsync(() -> {
                final StopWatch stopwatch = new StopWatch();
                stopwatch.start();

                Optional<List<ClusterDto>> result = Optional.empty();
                try {
                    result = Optional.of(clusteringService.generateClusters(clusterRequestDto.getMapName()));
                } catch (Exception e) {
                    log.error("Async-Exception", e);
                } finally {
                    mutexService.release(String.format(mutexFormat, clusterRequestDto.getMapName()));
                    stopwatch.stop();
                    log.info("Generated clusters for map {} in {} ms.", clusterRequestDto.getMapName(), stopwatch.getTotalTimeMillis());
                }

                return result;
            }, executorProvider.provideClusterGeneratorExecutor());
        }

        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .cacheControl(CacheControl.noCache())
                .build();
    }

}