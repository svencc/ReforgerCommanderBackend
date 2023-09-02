package com.recom.api.commandbus;

import com.recom.api.commons.HttpCommons;
import com.recom.dto.command.CommandBusRequestDto;
import com.recom.dto.command.CommandBusResponseDto;
import com.recom.persistence.command.CommandPersistenceLayer;
import com.recom.service.AssertionService;
import com.recom.service.ReforgerPayloadParserService;
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
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "CommandBus")
@RequestMapping("/api/v1/command-bus")
public class CommandBusController {

    @NonNull
    private final AssertionService assertionService;
    @NonNull
    private final ReforgerPayloadParserService payloadParser;
    @NonNull
    private final CommandPersistenceLayer commandPersistenceLayer;

    @Operation(
            summary = "Get a list of commands",
            description = "Gets all map specific, latest commands of a type.",
            security = @SecurityRequirement(name = HttpCommons.BEARER_AUTHENTICATION_REQUIREMENT)
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = HttpCommons.OK_CODE, description = HttpCommons.OK),
            @ApiResponse(responseCode = HttpCommons.UNAUTHORIZED_CODE, description = HttpCommons.UNAUTHORIZED, content = @Content())
    })
    @PostMapping(path = "/form", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<CommandBusResponseDto> getCommandsForm(
            @RequestParam(required = true)
            @NonNull final Map<String, String> payload
    ) {
        log.debug("Requested POST /api/v1/map/renderer/form (FORM)");

        return getCommandsJSON(payloadParser.parseValidated(payload, CommandBusRequestDto.class));
    }

    @Operation(
            summary = "Get a list of commands",
            description = "Gets all map specific, latest commands of a type.",
            security = @SecurityRequirement(name = HttpCommons.BEARER_AUTHENTICATION_REQUIREMENT)
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = HttpCommons.OK_CODE, description = HttpCommons.OK),
            @ApiResponse(responseCode = HttpCommons.UNAUTHORIZED_CODE, description = HttpCommons.UNAUTHORIZED, content = @Content())
    })
    @PostMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommandBusResponseDto> getCommandsJSON(
            @RequestBody(required = true)
            @NonNull @Valid final CommandBusRequestDto mapRendererRequestDto
    ) {
        log.debug("Requested POST /api/v1/map/command-bus (JSON)");

//        assertionService.assertMapExists(mapRendererRequestDto.getMapName());

        return ResponseEntity.status(HttpStatus.OK)
                .cacheControl(CacheControl.noCache())
                .body(
                        CommandBusResponseDto.builder()
                                .commands(commandPersistenceLayer.findAllMapSpecificCommands(mapRendererRequestDto.getMapName()))
                                .build()
                );
    }

}
