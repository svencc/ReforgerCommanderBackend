package com.recom.api.map;

import com.recom.api.commons.HttpCommons;
import com.recom.dto.map.scanner.TransactionIdentifierDto;
import com.recom.dto.map.scanner.TransactionalEntityPackageDto;
import com.recom.service.ReforgerPayloadParserService;
import com.recom.service.map.scanner.MapEntityTransactionService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "MapScanner")
@RequestMapping("/api/v1/map/scanner")
public class MapScannerController {

    @NonNull
    private final MapEntityTransactionService mapEntityTransactionService;
    @NonNull
    private final ReforgerPayloadParserService payloadParser;


    @Operation(
            summary = "Starts a map-scanner transaction",
            description = "Starts a map-scanner session.",
            security = @SecurityRequirement(name = HttpCommons.BEARER_AUTHENTICATION_REQUIREMENT)
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = HttpCommons.OK_CODE, description = HttpCommons.OK)
    })
    @PostMapping(path = "/transaction/open", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Void> startTransaction(
            @RequestParam(required = true)
            @NonNull final Map<String, String> payload
    ) {
        log.info("Requested POST /api/v1/map/scanner/transaction/open");

        mapEntityTransactionService.openTransaction(payloadParser.parseValidated(payload, TransactionIdentifierDto.class));
        return ResponseEntity.status(HttpStatus.OK)
                .cacheControl(CacheControl.noCache())
                .build();
    }

    @Operation(
            summary = "Ends a map-scanner transaction",
            description = "Ends a map-scanner session.",
            security = @SecurityRequirement(name = HttpCommons.BEARER_AUTHENTICATION_REQUIREMENT)
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = HttpCommons.OK_CODE, description = HttpCommons.OK),
            @ApiResponse(responseCode = HttpCommons.UNAUTHORIZED_CODE, description = HttpCommons.UNAUTHORIZED, content = @Content())
    })
    @PostMapping(path = "/transaction/commit", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Void> commitTransaction(
            @RequestParam(required = true)
            @NonNull final Map<String, String> payload
    ) {
        log.info("Requested POST /api/v1/map/scanner/transaction/commit");

        mapEntityTransactionService.commitTransaction(payloadParser.parseValidated(payload, TransactionIdentifierDto.class));
        return ResponseEntity.status(HttpStatus.OK)
                .cacheControl(CacheControl.noCache())
                .build();
    }

    @Operation(
            summary = "Transfer map-entities-package",
            description = "Receives a scanned package of map-entities.",
            security = @SecurityRequirement(name = HttpCommons.BEARER_AUTHENTICATION_REQUIREMENT)
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = HttpCommons.OK_CODE, description = HttpCommons.OK),
            @ApiResponse(responseCode = HttpCommons.UNAUTHORIZED_CODE, description = HttpCommons.UNAUTHORIZED, content = @Content())
    })
    @PostMapping(path = "/transaction/entities", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Void> transmitEntityPackage(
            @RequestParam(required = true)
            @NonNull final Map<String, String> payload
    ) {
        log.info("Requested POST /api/v1/map/scanner/transaction/entities");

        mapEntityTransactionService.addMapEntitiesPackage(payloadParser.parseValidated(payload, TransactionalEntityPackageDto.class));
        return ResponseEntity.status(HttpStatus.OK)
                .cacheControl(CacheControl.noCache())
                .build();
    }

}
