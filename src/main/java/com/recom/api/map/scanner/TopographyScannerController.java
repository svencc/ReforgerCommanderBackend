package com.recom.api.map.scanner;

import com.recom.api.commons.HttpCommons;
import com.recom.dto.map.scanner.TransactionIdentifierDto;
import com.recom.dto.map.scanner.topography.TransactionalMapTopographyEntityPackageDto;
import com.recom.service.ReforgerPayloadParserService;
import com.recom.service.map.scanner.MapTopographyTransactionService;
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
@Tag(name = "MapTopographyScanner")
@RequestMapping("/api/v1/scanner/topography")
public class TopographyScannerController {

    @NonNull
    private final MapTopographyTransactionService mapTopographyTransactionService;
    @NonNull
    private final ReforgerPayloadParserService payloadParser;


    @Operation(
            summary = "Starts a map-topography-scanner transaction",
            description = "Starts a map-topography-scanner session.",
            security = @SecurityRequirement(name = HttpCommons.BEARER_AUTHENTICATION_REQUIREMENT)
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = HttpCommons.OK_CODE, description = HttpCommons.OK)
    })
    @PostMapping(path = "/transaction/open", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Void> startMapTopographyTransaction(
            @RequestParam(required = true)
            @NonNull final Map<String, String> payload
    ) {
        log.debug("Requested POST /api/v1/scanner/topography/transaction/open");

        mapTopographyTransactionService.openTransaction(payloadParser.parseValidated(payload, TransactionIdentifierDto.class));
        return ResponseEntity.status(HttpStatus.OK)
                .cacheControl(CacheControl.noCache())
                .build();
    }

    @Operation(
            summary = "Ends a map-topography-scanner transaction",
            description = "Ends a map-topography-scanner session.",
            security = @SecurityRequirement(name = HttpCommons.BEARER_AUTHENTICATION_REQUIREMENT)
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = HttpCommons.OK_CODE, description = HttpCommons.OK),
            @ApiResponse(responseCode = HttpCommons.UNAUTHORIZED_CODE, description = HttpCommons.UNAUTHORIZED, content = @Content())
    })
    @PostMapping(path = "/transaction/commit", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Void> commitMapTopographyTransaction(
            @RequestParam(required = true)
            @NonNull final Map<String, String> payload
    ) {
        log.debug("Requested POST /api/v1/scanner/topography/transaction/commit");

        mapTopographyTransactionService.commitTransaction(payloadParser.parseValidated(payload, TransactionIdentifierDto.class));
        return ResponseEntity.status(HttpStatus.OK)
                .cacheControl(CacheControl.noCache())
                .build();
    }

    @Operation(
            summary = "Transfer map-topography-scanner-package",
            description = "Receives a scanned package of map-topography-entities.",
            security = @SecurityRequirement(name = HttpCommons.BEARER_AUTHENTICATION_REQUIREMENT)
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = HttpCommons.OK_CODE, description = HttpCommons.OK),
            @ApiResponse(responseCode = HttpCommons.UNAUTHORIZED_CODE, description = HttpCommons.UNAUTHORIZED, content = @Content())
    })
    @PostMapping(path = "/transaction/entities", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Void> transmitMapTopographyPackage(
            @RequestParam(required = true)
            @NonNull final Map<String, String> payload
    ) {
        log.debug("Requested POST /api/v1/scanner/topography/transaction/entities");

        mapTopographyTransactionService.addMapEntitiesPackage(payloadParser.parseValidated(payload, TransactionalMapTopographyEntityPackageDto.class));
        return ResponseEntity.status(HttpStatus.OK)
                .cacheControl(CacheControl.noCache())
                .build();
    }

}
