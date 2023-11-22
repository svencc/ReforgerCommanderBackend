package com.recom.api.map;

import com.recom.api.commons.HttpCommons;
import com.recom.dto.map.renderer.MapRenderResponseDto;
import com.recom.dto.map.renderer.MapRendererRequestDto;
import com.recom.entity.GameMap;
import com.recom.service.AssertionService;
import com.recom.service.ReforgerPayloadParserService;
import com.recom.service.dbcached.AsyncCacheableRequestProcessor;
import com.recom.service.map.MapRendererService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "MapRenderer")
@RequestMapping("/api/v1/map/renderer")
public class MapRendererController {

    @NonNull
    private final AssertionService assertionService;
    @NonNull
    private final ReforgerPayloadParserService payloadParser;
    @NonNull
    private final MapRendererService mapRendererService;
    @NonNull
    private final AsyncCacheableRequestProcessor asyncCacheableRequestProcessor;


    @Operation(
            summary = "Generates com.recom.dto.map render commands",
            description = "Calculates com.recom.dto.map render commands based on the given com.recom.dto.map.",
            security = @SecurityRequirement(name = HttpCommons.BEARER_AUTHENTICATION_REQUIREMENT)
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = HttpCommons.OK_CODE, description = HttpCommons.OK),
            @ApiResponse(responseCode = HttpCommons.UNAUTHORIZED_CODE, description = HttpCommons.UNAUTHORIZED, content = @Content())
    })
    @PostMapping(path = "/form", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<MapRenderResponseDto> generateMapRenderingsForm(
            @RequestParam(required = true)
            @NonNull final Map<String, String> payload
    ) {
        log.debug("Requested POST /api/v1/com.recom.dto.map/renderer/form (FORM)");

        return generateMapRenderingsJSON(payloadParser.parseValidated(payload, MapRendererRequestDto.class));
    }

    @Operation(
            summary = "Generates com.recom.dto.map render commands",
            description = "Calculates com.recom.dto.map render commands based on the given com.recom.dto.map.",
            security = @SecurityRequirement(name = HttpCommons.BEARER_AUTHENTICATION_REQUIREMENT)
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = HttpCommons.OK_CODE, description = HttpCommons.OK),
            @ApiResponse(responseCode = HttpCommons.UNAUTHORIZED_CODE, description = HttpCommons.UNAUTHORIZED, content = @Content())
    })
    @PostMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MapRenderResponseDto> generateMapRenderingsJSON(
            @RequestBody(required = true)
            @NonNull @Valid final MapRendererRequestDto mapRendererRequestDto
    ) {
        log.debug("Requested POST /api/v1/com.recom.dto.map/renderer (JSON)");

        final GameMap gameMap = assertionService.provideMap(mapRendererRequestDto.getMapName());

        return asyncCacheableRequestProcessor.processRequest(
                MapRendererService.MAP_RENDERER_CACHE_NAME,
                mapRendererRequestDto.getMapName(),
                () -> mapRendererService.renderMap(gameMap)
        );
    }

}