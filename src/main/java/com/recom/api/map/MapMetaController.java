package com.recom.api.map;

import com.recom.api.commons.HttpCommons;
import com.recom.dto.map.meta.MapMetaDto;
import com.recom.service.map.AssertionService;
import com.recom.service.map.MapMetaDataService;
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
    private final MapMetaDataService mapMetaDataService;


    @Operation(
            summary = "Gets a list of scanned maps meta data.",
            description = "Return a list of maps with meta information."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = HttpCommons.OK_CODE, description = HttpCommons.OK)
    })
    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MapMetaDto>> mapMeta(
            @RequestParam(required = false, name = "mapName")
            @NonNull final Optional<String> mapNameOpt
    ) {
        if (mapNameOpt.isPresent()) {
            log.debug("Requested GET /api/v1/map/meta?mapName={}", mapNameOpt.get());
            assertionService.assertMapExists(mapNameOpt.get());

            return ResponseEntity.status(HttpStatus.OK)
                    .cacheControl(CacheControl.noCache())
                    .body(List.of(mapMetaDataService.provideMapMeta(mapNameOpt.get())));
        } else {
            log.debug("Requested GET /api/v1/map/meta");

            return ResponseEntity.status(HttpStatus.OK)
                    .cacheControl(CacheControl.noCache())
                    .body(mapMetaDataService.provideMapMetaList());
        }
    }

}
