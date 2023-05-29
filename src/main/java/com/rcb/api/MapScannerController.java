package com.rcb.api;

import com.rcb.api.commons.HttpCommons;
import com.rcb.dto.map.scanner.TransactionIdentifierDto;
import com.rcb.dto.map.scanner.TransactionalEntityPackageDto;
import com.rcb.service.ReforgerPayloadParserService;
import com.rcb.service.map.MapEntityTransactionService;
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

@Slf4j
@RestController
@Tag(name = "MapScanner")
@RequiredArgsConstructor
@RequestMapping("/api/v1/map-scanner")
public class MapScannerController {

    @NonNull
    private final MapEntityTransactionService mapEntityTransactionService;
    @NonNull
    private final ReforgerPayloadParserService payloadParser;


    @Operation(
            summary = "Starts a map-scanner transaction",
            description = "Starts a map-scanner session."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = HttpCommons.OK_CODE, description = HttpCommons.OK)
    })
    @PostMapping(path = "/transaction/open", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Void> startTransaction(
            @RequestParam(required = true)
            @NonNull final Map<String, String> payload
    ) {
        log.debug("Requested POST /api/v1/map-scanner/transaction/open");

        mapEntityTransactionService.openTransaction(payloadParser.parseValidated(payload, TransactionIdentifierDto.class));
        return ResponseEntity.status(HttpStatus.OK)
                .cacheControl(CacheControl.noCache())
                .build();

/*
        return ReforgerPayload.parse(payload, TransactionIdentifierDto.class)
                .map(openTransactionIdentifier -> {
                    mapEntityTransactionService.openTransaction(openTransactionIdentifier);
                    return ResponseEntity.status(HttpStatus.OK)
                            .cacheControl(CacheControl.noCache())
                            .<Void>build();
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .cacheControl(CacheControl.noCache())
                        .<Void>build());
 */
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
            @RequestParam(required = true)
            @NonNull final Map<String, String> payload
    ) {
        log.debug("Requested POST /api/v1/map-scanner/transaction/commit");

        mapEntityTransactionService.commitTransaction(payloadParser.parseValidated(payload, TransactionIdentifierDto.class));
        return ResponseEntity.status(HttpStatus.OK)
                .cacheControl(CacheControl.noCache())
                .build();
        /*
        return ReforgerPayload.parse(payload, TransactionIdentifierDto.class)
                .map(openTransactionIdentifier -> {
                    mapEntityTransactionService.commitTransaction(openTransactionIdentifier);
                    return ResponseEntity.status(HttpStatus.OK)
                            .cacheControl(CacheControl.noCache())
                            .<Void>build();
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .cacheControl(CacheControl.noCache())
                        .<Void>build());
         */
    }

    @Operation(
            summary = "Transfer map-entities-package.",
            description = "Receives a scanned package of map-entities."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = HttpCommons.OK_CODE, description = HttpCommons.OK)
    })
    @PostMapping(path = "/map-entities", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Void> transmitEntityPackage(
            @RequestParam(required = true)
            @NonNull final Map<String, String> payload
    ) {
        log.debug("Requested POST /api/v1/test/map-entities");

        mapEntityTransactionService.addMapEntitiesPackage(payloadParser.parseValidated(payload, TransactionalEntityPackageDto.class));
        return ResponseEntity.status(HttpStatus.OK)
                .cacheControl(CacheControl.noCache())
                .build();
        /*
        return ReforgerPayload.parse(payload, TransactionalEntityPackageDto.class)
                .map(entityPackageDto -> {
                    mapEntityTransactionService.addMapEntitiesPackage(entityPackageDto);
                    return ResponseEntity.status(HttpStatus.OK)
                            .cacheControl(CacheControl.noCache())
                            .<Void>build();
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .cacheControl(CacheControl.noCache())
                        .<Void>build());
         */
    }

}
