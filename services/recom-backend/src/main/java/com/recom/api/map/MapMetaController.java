package com.recom.api.map;

import com.recom.api.commons.HttpCommons;
import com.recom.dto.map.meta.MapMetaDto;
import com.recom.entity.map.GameMap;
import com.recom.service.AssertionService;
import com.recom.service.map.GameMapService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
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
    private final GameMapService gameMapService;


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
    public ResponseEntity<List<MapMetaDto>> mapMeta(
            @RequestParam(required = false, name = "mapName")
            @NonNull final Optional<String> maybeMapName
    ) {
        if (maybeMapName.isPresent()) {
            log.debug("Requested GET /api/v1/com.recom.dto.map/meta?mapName={}", maybeMapName.get());
            final GameMap gameMap = assertionService.provideMap(maybeMapName.get());

            return ResponseEntity.status(HttpStatus.OK)
                    .cacheControl(CacheControl.noCache())
                    .body(List.of(gameMapService.provideGameMapMetaData(gameMap)));
        } else {
            log.debug("Requested GET /api/v1/com.recom.dto.map/meta");

            return ResponseEntity.status(HttpStatus.OK)
                    .cacheControl(CacheControl.noCache())
                    .body(gameMapService.provideGameMapMetaList());
        }
    }

}
