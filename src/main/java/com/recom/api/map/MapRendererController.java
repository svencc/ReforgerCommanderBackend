package com.recom.api.map;

import com.recom.api.commons.HttpCommons;
import com.recom.dto.map.renderer.MapRenderingsDto;
import com.recom.dto.map.renderer.MapRendererRequestDto;
import com.recom.service.ReforgerPayloadParserService;
import com.recom.service.map.MapRendererService;
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
@RequestMapping("/api/v1/map/renderer")
public class MapRendererController {

    @NonNull
    private final ReforgerPayloadParserService payloadParser;
    @NonNull
    private final MapRendererService mapRendererService;


    @Operation(
            summary = "Determines clusters of Town/City/Village and military relevant targets.",
            description = "Calculates city clusters. WIP - other cluster have to be added; db-based-config system is needed (per map)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = HttpCommons.OK_CODE, description = HttpCommons.OK)
    })
    @PostMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<MapRenderingsDto> generateMapRenderingsForm(
            @RequestParam(required = true)
            @NonNull final Map<String, String> payload
    ) {
        log.debug("Requested POST /api/v1/map/renderer (FORM)");

        return generateMapRenderingsJSON(payloadParser.parseValidated(payload, MapRendererRequestDto.class));
    }

    @Operation(
            summary = "Determines clusters of Town/City/Village and military relevant targets.",
            description = "Calculates city clusters. WIP - other cluster have to be added; db-based-config system is needed (per map)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = HttpCommons.OK_CODE, description = HttpCommons.OK)
    })
    @PostMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MapRenderingsDto> generateMapRenderingsJSON(
            @RequestBody(required = true)
            @NonNull final MapRendererRequestDto mapRendererRequestDto
    ) {
        log.debug("Requested POST /api/v1/map/renderer (JSON)");

        // 202 Accepted logic - async processing
        // Refactor ClustersController. Extract async processing to service

        return ResponseEntity.status(HttpStatus.OK)
                .cacheControl(CacheControl.noCache())
                .body(mapRendererService.renderMap(mapRendererRequestDto));
    }
}