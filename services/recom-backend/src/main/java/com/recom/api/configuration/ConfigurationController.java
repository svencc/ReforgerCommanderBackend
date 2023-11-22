package com.recom.api.configuration;

import com.recom.api.commons.HttpCommons;
import com.recom.dto.configuration.OverridableConfigurationDto;
import com.recom.dto.configuration.OverrideConfigurationDto;
import com.recom.entity.GameMap;
import com.recom.event.event.async.cache.CacheResetAsyncEvent;
import com.recom.service.AssertionService;
import com.recom.service.configuration.ConfigurationRESTManagementService;
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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "Configuration")
@RequestMapping("/api/v1/configuration")
public class ConfigurationController {

    @NonNull
    private final AssertionService assertionService;
    @NonNull
    private final ConfigurationRESTManagementService configurationRESTManagementService;
    @NonNull
    private final ApplicationEventPublisher applicationEventPublisher;


    @Operation(
            summary = "Get a list of overridable com.recom.configuration settings",
            description = "Gets all or com.recom.dto.map specific com.recom.configuration data, with default value and com.recom.dto.map specific overridden values.",
            security = @SecurityRequirement(name = HttpCommons.BEARER_AUTHENTICATION_REQUIREMENT)
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = HttpCommons.OK_CODE, description = HttpCommons.OK),
            @ApiResponse(responseCode = HttpCommons.UNAUTHORIZED_CODE, description = HttpCommons.UNAUTHORIZED, content = @Content())
    })
    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<OverridableConfigurationDto>> getConfigurations(
            @RequestParam(required = false, name = "mapName")
            @NonNull final Optional<String> maybeMapName
    ) {
        log.debug("Requested GET /api/v1/com.recom.dto.map/com.recom.configuration");

        final AtomicReference<GameMap> mapMeta = new AtomicReference<>();
        maybeMapName.ifPresent((mapName) -> mapMeta.set(assertionService.provideMap(mapName)));

        return maybeMapName.map((final String mapName) -> ResponseEntity.status(HttpStatus.OK)
                        .cacheControl(CacheControl.noCache())
                        .body(configurationRESTManagementService.provideAllExistingConfigurationValues(mapMeta.get())))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.OK)
                        .cacheControl(CacheControl.noCache())
                        .body(configurationRESTManagementService.provideAllExistingConfigurationValues())
                );
    }

    @Operation(
            summary = "Set a list of overridable com.recom.configuration settings",
            description = "Sets com.recom.dto.map specific com.recom.configuration data, with com.recom.dto.map specific overridden values.",
            security = @SecurityRequirement(name = HttpCommons.BEARER_AUTHENTICATION_REQUIREMENT)
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = HttpCommons.OK_CODE, description = HttpCommons.OK),
            @ApiResponse(responseCode = HttpCommons.UNAUTHORIZED_CODE, description = HttpCommons.UNAUTHORIZED, content = @Content())
    })
    @PutMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<OverridableConfigurationDto>> setConfigurations(
            @RequestParam(required = true)
            @NonNull final String mapName,

            @RequestBody(required = true)
            @Valid @NonNull final List<OverrideConfigurationDto> overrideList
    ) {
        log.debug("Requested POST /api/v1/com.recom.dto.map/com.recom.configuration");

        final GameMap gameMap = assertionService.provideMap(mapName);
        configurationRESTManagementService.updateOverrides(gameMap, overrideList);
        applicationEventPublisher.publishEvent(new CacheResetAsyncEvent());

        return ResponseEntity.status(HttpStatus.OK)
                .cacheControl(CacheControl.noCache())
                .body(configurationRESTManagementService.provideAllExistingConfigurationValues(gameMap));
    }

}
