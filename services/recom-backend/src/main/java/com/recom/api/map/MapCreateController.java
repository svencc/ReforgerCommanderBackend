package com.recom.api.map;

import com.recom.api.commons.HttpCommons;
import com.recom.dto.map.create.MapCreateRequestDto;
import com.recom.dto.map.create.MapCreateResponseDto;
import com.recom.entity.map.GameMap;
import com.recom.security.account.RECOMAccount;
import com.recom.security.account.RECOMAuthorities;
import com.recom.service.ReforgerPayloadParserService;
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
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@Tag(name = "Maps")
@RequiredArgsConstructor
@RequestMapping("/api/v1/map/create")
public class MapCreateController {

    @NonNull
    private final ReforgerPayloadParserService payloadParser;
    @NonNull
    private final GameMapService gameMapService;


    @Operation(
            summary = "Create a com.recom.dto.map",
            description = "Creates a com.recom.dto.map with given name",
            security = @SecurityRequirement(name = HttpCommons.BEARER_AUTHENTICATION_REQUIREMENT)
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = HttpCommons.OK_CODE, description = HttpCommons.OK),
            @ApiResponse(responseCode = HttpCommons.UNAUTHORIZED_CODE, description = HttpCommons.UNAUTHORIZED, content = @Content())
    })
    @PostMapping(path = "/form", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<MapCreateResponseDto> mapCreateForm(
            @AuthenticationPrincipal final RECOMAccount account,
            @RequestParam(required = true)
            @NonNull final Map<String, String> payload
    ) {
        log.debug("Requested POST /api/v1/com.recom.dto.map/create/form (FORM)");

        return mapCreate(account, payloadParser.parseValidated(payload, MapCreateRequestDto.class));
    }

    @Operation(
            summary = "Create a com.recom.dto.map",
            description = "Creates a com.recom.dto.map with given name",
            security = @SecurityRequirement(name = HttpCommons.BEARER_AUTHENTICATION_REQUIREMENT)
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = HttpCommons.OK_CODE, description = HttpCommons.OK),
            @ApiResponse(responseCode = HttpCommons.UNAUTHORIZED_CODE, description = HttpCommons.UNAUTHORIZED, content = @Content())
    })
    @PostMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({RECOMAuthorities.EVERYBODY})
    public ResponseEntity<MapCreateResponseDto> mapCreate(
            @AuthenticationPrincipal final RECOMAccount account,
            @RequestBody final MapCreateRequestDto mapCreateRequestDto
    ) {
        log.debug("Requested GET /api/v1/com.recom.dto.map/create (JSON)");

        final GameMap gameMap = gameMapService.provideGameMap(mapCreateRequestDto.getMapName())
                .orElseGet(() -> gameMapService.create(mapCreateRequestDto));

        return ResponseEntity.status(HttpStatus.OK)
                .cacheControl(CacheControl.noCache())
                .body(MapCreateResponseDto.builder()
                        .mapName(gameMap.getName())
                        .mapExists(true)
                        .build()
                );
    }

}
