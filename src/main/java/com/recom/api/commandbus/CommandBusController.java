package com.recom.api.commandbus;

import com.recom.api.commons.HttpCommons;
import com.recom.dto.command.CommandDto;
import com.recom.persistence.command.CommandPersistenceLayer;
import com.recom.service.AssertionService;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CommandDto>> getCommands(
            @RequestParam(required = true)
            @NonNull final String mapName
    ) {
        log.debug("Requested GET /api/v1/map/command-bus");

        // @TODO: re-enable after development
//        assertionService.assertMapExists(mapName);

        final List<CommandDto> allMapSpecificCommands = commandPersistenceLayer.findAllMapSpecificCommands(mapName);

        if (allMapSpecificCommands.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .cacheControl(CacheControl.noCache())
                    .build();
        } else {
            return ResponseEntity.status(HttpStatus.OK)
                    .cacheControl(CacheControl.noCache())
                    .body(allMapSpecificCommands);
        }

    }

}
