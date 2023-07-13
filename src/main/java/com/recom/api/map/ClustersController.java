package com.recom.api.map;

import com.recom.api.commons.HttpCommons;
import com.recom.dto.map.cluster.ClusterDto;
import com.recom.dto.map.cluster.ClusterListDto;
import com.recom.dto.map.cluster.MapClusterRequestDto;
import com.recom.service.AssertionService;
import com.recom.service.MutexService;
import com.recom.service.ReforgerPayloadParserService;
import com.recom.service.map.cluster.ClusteringService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "MapCluster")
@RequestMapping("/api/v1/map/clusters")
public class ClustersController {

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

        final String cacheName = "MapEntityPersistenceLayer.generateClusters";
        final String mutexFormat = "ClustersController.generateClustersJSON#%1s";

        final Optional<List<ClusterDto>> cachedValue = Optional.ofNullable(cacheManager.getCache(cacheName))
                .flatMap(cache -> {
                    try {
                        final Optional<Cache.ValueWrapper> valueWrapper = Optional.ofNullable(cache.get(clusterRequestDto.getMapName()));
                        if (valueWrapper.isPresent()) {
                            return Optional.ofNullable((List<ClusterDto>) valueWrapper.get().get());
                        } else {
                            return Optional.empty();
                        }
                    } catch (NoSuchElementException noSuchElementException) {
                        return Optional.empty();
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                        return Optional.empty();
                    }
                });

        if (cachedValue.isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .cacheControl(CacheControl.noCache())
                    .body(ClusterListDto.builder().clusterList(cachedValue.get()).build());
        } else {
            boolean claimed = mutexService.claim(String.format(mutexFormat, clusterRequestDto.getMapName()));
            if (claimed) {
                log.info("Generating clusters for map {}.", clusterRequestDto.getMapName());

                CompletableFuture.supplyAsync(() -> {
                    try {
                        final List<ClusterDto> clusterDtos = clusteringService.generateClusters(clusterRequestDto.getMapName());
                        // @ TODO: test this
//                        cacheManager.getCache(cacheName).put(clusterRequestDto.getMapName(), clusterDtos);
                    } catch (Exception e) {
                        log.error("Async-Exception", e);
                    } finally {
                        mutexService.release(String.format(mutexFormat, clusterRequestDto.getMapName()));
                        log.info("Generated clusters for map {}.", clusterRequestDto.getMapName());
                    }

                    return "Result of the asynchronous computation";
                });

                return ResponseEntity.status(HttpStatus.ACCEPTED)
                        .cacheControl(CacheControl.noCache())
                        .build();
            } else {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .cacheControl(CacheControl.noCache())
                        .build();
            }
        }
    }

}
