package com.recom.api.map;

import com.recom.api.commons.HttpCommons;
import com.recom.dto.map.meta.MapMetaDto;
import com.recom.service.AssertionService;
import com.recom.service.dbcached.AsyncCacheableRequestProcessor;
import com.recom.service.map.MapMetaDataService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

@Slf4j
@RestController
@Tag(name = "MapMeta")
@RequiredArgsConstructor
@RequestMapping("/api/v1/map/meta")
public class MapMetaController {

    @NonNull
    private final AssertionService assertionService;
    @NonNull
    private final MapMetaDataService mapMetaDataService;
    @NonNull
    private final AsyncCacheableRequestProcessor asyncCacheableRequestProcessor;


    @Operation(
            summary = "Gets a list of scanned maps meta data",
            description = "Return a list of maps with meta information.",
            security = @SecurityRequirement(name = HttpCommons.BEARER_AUTHENTICATION_REQUIREMENT)
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = HttpCommons.OK_CODE, description = HttpCommons.OK),
            @ApiResponse(responseCode = HttpCommons.UNAUTHORIZED_CODE, description = HttpCommons.UNAUTHORIZED, content = @Content())
    })
    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ArrayList<MapMetaDto>> mapMeta(
            @RequestParam(required = false, name = "mapName")
            @NonNull final Optional<String> mapNameOpt
    ) {
        if (mapNameOpt.isPresent()) {
            log.debug("Requested GET /api/v1/map/meta?mapName={}", mapNameOpt.get());
            assertionService.assertMapExists(mapNameOpt.get());

            return asyncCacheableRequestProcessor.processRequestWithAsyncCache(
                    MapMetaDataService.PROVIDEMAPMETALIST_PROVIDEMAPMETA_CACHE,
                    mapNameOpt.get(),
                    () -> Optional.of(
                            new ArrayList<>(Collections.singletonList(mapMetaDataService.provideMapMeta(mapNameOpt.get())))
                    )
            );
        } else {
            log.debug("Requested GET /api/v1/map/meta");

            return asyncCacheableRequestProcessor.processRequestWithAsyncCache(
                    MapMetaDataService.PROVIDEMAPMETALIST_PROVIDEMAPMETALIST_CACHE,
                    "",
                    () -> Optional.of(
                            mapMetaDataService.provideMapMetaList()
                    )
            );
        }
    }

}
