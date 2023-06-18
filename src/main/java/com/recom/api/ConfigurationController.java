package com.recom.api;

import com.recom.api.commons.HttpCommons;
import com.recom.dto.configuration.get.ConfigurationListDto;
import com.recom.dto.configuration.post.OverrideConfigurationListDto;
import com.recom.service.configuration.ConfigurationRESTManagementService;
import io.swagger.v3.oas.annotations.Operation;
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

import java.util.Optional;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "Configuration")
@RequestMapping("/api/v1/configuration")
public class ConfigurationController {

    @NonNull
    private final ConfigurationRESTManagementService configurationRESTManagementService;

    @Operation(
            summary = "Get a list of overridable configuration settings.",
            description = "Gets all or map specific configuration data, with with default value and map specific overridden values."
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

        return mapNameOpt.map((final String mapName) -> ResponseEntity.status(HttpStatus.OK)
                        .cacheControl(CacheControl.noCache())
                        .body(configurationRESTManagementService.provideAllExistingConfigurationValues(mapName)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.OK)
                        .cacheControl(CacheControl.noCache())
                        .body(configurationRESTManagementService.provideAllExistingConfigurationValues())
                );
    }

    @Operation(
            summary = "Get a list of overridable configuration settings.",
            description = "Gets all or map specific configuration data, with with default value and map specific overridden values."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = HttpCommons.OK_CODE, description = HttpCommons.OK)
    })
    @PostMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ConfigurationListDto> postConfigurations(
            @RequestBody(required = false)
            @Valid @NonNull final OverrideConfigurationListDto overrideList
    ) {
        log.debug("Requested POST /api/v1/map/configuration");

        configurationRESTManagementService.updateOverrides(overrideList);

        return ResponseEntity.status(HttpStatus.OK)
                        .cacheControl(CacheControl.noCache())
                        .body(configurationRESTManagementService.provideAllExistingConfigurationValues(overrideList.getMapName()));

    }

}
