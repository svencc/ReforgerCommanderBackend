package com.rcb.api;

import com.rcb.dto.mapScanner.MapScannerEntityDto;
import com.rcb.util.ReforgerPayload;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@Tag(name = "MapScanner")
@RequiredArgsConstructor
@RequestMapping("/api/v1/map-scanner")
public class MapScannerController {

    @Operation(
            summary = "POST map-entity call",
            description = "Receives a scanned map-entity.",
            tags = "test"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "O.K.")
    })
    @PostMapping(path = "/map-entity", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Void> startMapScanner(
            @RequestParam Map<String, String> payload
    ) {
        log.debug("Requested POST /api/v1/test/map-entity");

        final Optional<MapScannerEntityDto> mapScannerEntityDtoOpt = ReforgerPayload.parse(payload, MapScannerEntityDto.class);
        log.debug(" -> Received: {}", mapScannerEntityDtoOpt);

        return ResponseEntity.status(HttpStatus.OK)
                .cacheControl(CacheControl.noCache())
                .build();
    }

}
