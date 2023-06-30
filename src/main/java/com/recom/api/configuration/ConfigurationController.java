package com.recom.api.configuration;

import com.recom.api.commons.HttpCommons;
import com.recom.dto.configuration.get.ConfigurationListDto;
import com.recom.dto.configuration.post.OverrideConfigurationListDto;
import com.recom.event.event.async.cache.CacheResetAsyncEvent;
import com.recom.service.configuration.ConfigurationRESTManagementService;
import com.recom.service.AssertionService;
import com.recom.service.map.MapMetaDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

import java.util.Optional;

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
    private final MapMetaDataService mapMetaDataService;
    @NonNull
    private final ConfigurationRESTManagementService configurationRESTManagementService;
    @NonNull
    private final ApplicationEventPublisher applicationEventPublisher;

    @Operation(
            summary = "Get a list of overridable configuration settings.",
            description = "Gets all or map specific configuration data, with default value and map specific overridden values."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = HttpCommons.OK_CODE, description = HttpCommons.OK)
    })
    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ConfigurationListDto> getConfigurations(
            @RequestParam(required = false, name = "mapName")
            @NonNull final Optional<String> mapNameOpt
    ) {
        log.debug("Requested GET /api/v1/map/configuration");

        mapNameOpt.ifPresent(assertionService::assertMapExists);

        return mapNameOpt.map((final String mapName) -> ResponseEntity.status(HttpStatus.OK)
                        .cacheControl(CacheControl.noCache())
                        .body(configurationRESTManagementService.provideAllExistingConfigurationValues(mapName)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.OK)
                        .cacheControl(CacheControl.noCache())
                        .body(configurationRESTManagementService.provideAllExistingConfigurationValues())
                );
    }

    @Operation(
            summary = "Set a list of overridable configuration settings.",
            description = "Sets map specific configuration data, with map specific overridden values."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = HttpCommons.OK_CODE, description = HttpCommons.OK)
    })
    @PostMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ConfigurationListDto> postConfigurations(
            @RequestBody(required = false)
            @Valid @NonNull final OverrideConfigurationListDto overrideList
    ) {
        log.debug("Requested POST /api/v1/map/configuration");

        assertionService.assertMapExists(overrideList.getMapName());
        configurationRESTManagementService.updateOverrides(overrideList);
        applicationEventPublisher.publishEvent(new CacheResetAsyncEvent());

        return ResponseEntity.status(HttpStatus.OK)
                .cacheControl(CacheControl.noCache())
                .body(configurationRESTManagementService.provideAllExistingConfigurationValues(overrideList.getMapName()));
    }

}
