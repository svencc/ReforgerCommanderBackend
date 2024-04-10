package com.recom.api.map;

import com.recom.api.commons.HttpCommons;
import com.recom.dto.map.exists.MapExistsRequestDto;
import com.recom.dto.map.exists.MapExistsResponseDto;
import com.recom.entity.map.GameMap;
import com.recom.exception.HttpNotFoundException;
import com.recom.security.account.RECOMAccount;
import com.recom.security.account.RECOMAuthorities;
import com.recom.service.AssertionService;
import com.recom.service.ReforgerPayloadParserService;
import com.recom.service.messagebus.chunkscanrequest.MapStructureChunkScanRequestNotificationService;
import com.recom.service.messagebus.chunkscanrequest.MapTopographyChunkScanRequestNotificationService;
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
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@RestController
@Tag(name = "Maps")
@RequiredArgsConstructor
@RequestMapping("/api/v1/map/exists")
public class MapExistsController {

    @NonNull
    private final AssertionService assertionService;
    @NonNull
    private final ReforgerPayloadParserService payloadParser;
    @NonNull
    private final MapTopographyChunkScanRequestNotificationService mapTopographyChunkScanRequestNotificationService;
    @NonNull
    private final MapStructureChunkScanRequestNotificationService mapStructureChunkScanRequestNotificationService;
    @NonNull
    private final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();


    @Operation(
            summary = "Determines existence of com.recom.dto.map",
            description = "Checks if com.recom.dto.map is already scanned and known in the system.",
            security = @SecurityRequirement(name = HttpCommons.BEARER_AUTHENTICATION_REQUIREMENT)
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = HttpCommons.OK_CODE, description = HttpCommons.OK),
            @ApiResponse(responseCode = HttpCommons.UNAUTHORIZED_CODE, description = HttpCommons.UNAUTHORIZED, content = @Content())
    })
    @PostMapping(path = "/form", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<MapExistsResponseDto> mapExistsForm(
            @AuthenticationPrincipal final RECOMAccount account,
            @RequestParam(required = true)
            @NonNull final Map<String, String> payload
    ) {
        log.debug("Requested POST /api/v1/com.recom.dto.map/exists/form (FORM)");

        return mapExists(account, payloadParser.parseValidated(payload, MapExistsRequestDto.class));
    }

    @Operation(
            summary = "Determines existence of com.recom.dto.map",
            description = "Checks if com.recom.dto.map is already scanned and known in the system.",
            security = @SecurityRequirement(name = HttpCommons.BEARER_AUTHENTICATION_REQUIREMENT)
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = HttpCommons.OK_CODE, description = HttpCommons.OK),
            @ApiResponse(responseCode = HttpCommons.UNAUTHORIZED_CODE, description = HttpCommons.UNAUTHORIZED, content = @Content())
    })
    @PostMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({RECOMAuthorities.EVERYBODY})
    public ResponseEntity<MapExistsResponseDto> mapExists(
            @AuthenticationPrincipal final RECOMAccount account,
            @RequestBody final MapExistsRequestDto mapExistsRequestDto
    ) {
        log.debug("Requested GET /api/v1/com.recom.dto.map/exists (JSON)");

        try {
            final GameMap gameMap = assertionService.provideMap(mapExistsRequestDto.getMapName());

            executorService.submit(() -> {
                try {
                    Thread.sleep(Duration.ofSeconds(1).toMillis());
                    mapTopographyChunkScanRequestNotificationService.requestMapTopographyChunkScan(gameMap);
                    mapStructureChunkScanRequestNotificationService.requestMapStructureChunkScan(gameMap);
                } catch (final Throwable t) {
                    log.error("Failed to notify map scan.", t);
                }
            });

            return ResponseEntity.status(HttpStatus.OK)
                    .cacheControl(CacheControl.noCache())
                    .body(MapExistsResponseDto.builder()
                            .mapName(gameMap.getName())
                            .mapExists(true)
                            .build());
        } catch (final HttpNotFoundException e) {
            return ResponseEntity.status(HttpStatus.OK)
                    .cacheControl(CacheControl.noCache())
                    .body(MapExistsResponseDto.builder()
                            .mapName(mapExistsRequestDto.getMapName())
                            .mapExists(false)
                            .build());
        }
    }

}
