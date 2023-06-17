package com.rcb.api.map;

import com.rcb.api.commons.HttpCommons;
import com.rcb.dto.map.meta.MapMetaListDto;
import com.rcb.service.ReforgerPayloadParserService;
import com.rcb.service.map.MapMetaDataService;
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
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "MapMeta")
@RequiredArgsConstructor
@RequestMapping("/api/v1/map/meta")
public class MapMetaController {

    @NonNull
    private final MapMetaDataService mapMetaDataService;


    @Operation(
            summary = "Gets a list of scanned maps.",
            description = "Return a list of maps with meta information."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = HttpCommons.OK_CODE, description = HttpCommons.OK)
    })
    @GetMapping(path = "/maps", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MapMetaListDto> mapMeta() {
        log.debug("Requested POST /api/v1/map/meta/maps");

        return ResponseEntity.status(HttpStatus.OK)
                .cacheControl(CacheControl.noCache())
                .body(mapMetaDataService.provideMapMetaList());
    }

}
