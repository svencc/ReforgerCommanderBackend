package com.rcb.api;

import com.rcb.api.commons.HttpCommons;
import com.rcb.dto.mapScanner.MapScannerEntityPackageDto;
import com.rcb.dto.mapScanner.TransactionIdentifierDto;
import com.rcb.service.persitence.MapEntityPersistenceLayer;
import com.rcb.util.ReforgerPayload;
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

    @NonNull
    private final MapEntityPersistenceLayer mapEntityPersistenceLayer;

    @Operation(
            summary = "Starts a map-scanner transaction",
            description = "Starts a map-scanner session."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = HttpCommons.OK_CODE, description = HttpCommons.OK)
    })
    @PostMapping(path = "/transaction/open", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Void> startTransaction(
            @RequestParam Map<String, String> payload
    ) {
        log.info("Requested POST /api/v1/map-scanner/transaction/open");

        final Optional<TransactionIdentifierDto> transactionIdentifierDto = ReforgerPayload.parse(payload, TransactionIdentifierDto.class);

        return ResponseEntity.status(HttpStatus.OK)
                .cacheControl(CacheControl.noCache())
                .build();
    }

    @Operation(
            summary = "Ends a map-scanner transaction",
            description = "Ends a map-scanner session."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = HttpCommons.OK_CODE, description = HttpCommons.OK)
    })
    @PostMapping(path = "/transaction/commit", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Void> commitTransaction(
            @RequestParam Map<String, String> payload
    ) {
        log.info("Requested POST /api/v1/map-scanner/transaction/commit");
        final Optional<TransactionIdentifierDto> transactionIdentifierDto = ReforgerPayload.parse(payload, TransactionIdentifierDto.class);

        return ResponseEntity.status(HttpStatus.OK)
                .cacheControl(CacheControl.noCache())
                .build();
    }

//
//
//    @Operation(
//            summary = "POST map-entity call",
//            description = "Receives a scanned map-entity."
//    )
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = HttpCommons.OK_CODE, description = HttpCommons.OK)
//    })
//    @PostMapping(path = "/map-entity", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
//    public ResponseEntity<Void> transmitEntity(
//            @RequestParam Map<String, String> payload
//    ) {
//        log.debug("Requested POST /api/v1/test/map-entity");
//
//        final Optional<MapScannerEntityDto> mapScannerEntityDtoOpt = ReforgerPayload.parse(payload, MapScannerEntityDto.class);
//        log.debug(" -> Received: {}", mapScannerEntityDtoOpt);
//
//        return ResponseEntity.status(HttpStatus.OK)
//                .cacheControl(CacheControl.noCache())
//                .build();
//    }

    @Operation(
            summary = "Transfer map-entities-package.",
            description = "Receives a scanned package of map-entities."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = HttpCommons.OK_CODE, description = HttpCommons.OK)
    })
    @PostMapping(path = "/map-entities", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Void> transmitEntities(
            @RequestParam Map<String, String> payload
    ) {
        log.debug("Requested POST /api/v1/test/map-entities");
        final Optional<MapScannerEntityPackageDto> mapScannerEntitiesOpt = ReforgerPayload.parse(payload, MapScannerEntityPackageDto.class);

        mapScannerEntitiesOpt.ifPresent(mapEntityPersistenceLayer::persistMapEntityPackage);


        return ResponseEntity.status(HttpStatus.OK)
                .cacheControl(CacheControl.noCache())
                .build();
    }

}
