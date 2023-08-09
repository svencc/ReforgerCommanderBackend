package com.recom.api;

import com.recom.api.commons.HttpCommons;
import com.recom.dto.authentication.AccountRequestDto;
import com.recom.dto.authentication.AuthenticationRequestDto;
import com.recom.dto.authentication.AuthenticationResponseDto;
import com.recom.service.AuthenticationService;
import com.recom.service.ReforgerPayloadParserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Authentication")
@RequestMapping("/api/v1/authenticate")
@Validated
public class AuthenticationController {

    @NonNull
    private final AuthenticationService authenticationService;
    @NonNull
    private final ReforgerPayloadParserService payloadParser;


    @Operation(
            summary = "Authenticate a user",
            description = "Authenticate a user via username and password; and returns a JWT token."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = HttpCommons.OK_CODE, description = HttpCommons.OK),
            @ApiResponse(responseCode = HttpCommons.UNAUTHORIZED_CODE, description = HttpCommons.UNAUTHORIZED, content = {@Content()}),
    })
    @PostMapping(path = "/form", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<AuthenticationResponseDto> authenticateForm(
            @RequestParam(required = true)
            @NonNull final Map<String, String> payload
    ) {
        log.debug("Requested POST /api/v1/authenticate (FORM)");

        return authenticateJSON(payloadParser.parseValidated(payload, AuthenticationRequestDto.class));
    }

    @Operation(
            summary = "Authenticate a user",
            description = "Authenticate a user via username and password; and returns a JWT token."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = HttpCommons.OK_CODE, description = HttpCommons.OK),
            @ApiResponse(responseCode = HttpCommons.UNAUTHORIZED_CODE, description = HttpCommons.UNAUTHORIZED, content = {@Content()}),
    })
    @PostMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthenticationResponseDto> authenticateJSON(
            @RequestBody()
            @Valid @NonNull final AuthenticationRequestDto authenticationRequestDto
    ) {
        log.debug("Requested POST /api/v1/authenticate (JSON)");

        return ResponseEntity.status(HttpStatus.OK)
                .cacheControl(CacheControl.noCache())
                .body(authenticationService.authenticateWith(authenticationRequestDto));
    }

    @Operation(
            summary = "Request a new account",
            description = "Create a new account."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = HttpCommons.OK_CODE, description = HttpCommons.OK),
            @ApiResponse(responseCode = HttpCommons.UNAUTHORIZED_CODE, description = HttpCommons.UNAUTHORIZED, content = {@Content()}),
    })
    @PostMapping(path = "/new-account", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AccountRequestDto> createAccount() {
        log.debug("Requested POST /api/v1/authenticate/new (JSON)");

        return ResponseEntity.status(HttpStatus.OK)
                .cacheControl(CacheControl.noCache())
                .body(authenticationService.createNewAccount());
    }

}
