package com.recom.api;

import com.recom.api.commons.HttpCommons;
import com.recom.dto.configuration.ConfigurationListDto;
import com.recom.service.configuration.ConfigurationRESTManagementService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Slf4j
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
                        .body(configurationRESTManagementService.provideAllExistingDefaultConfigurationValues(mapName)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.OK)
                        .cacheControl(CacheControl.noCache())
                        .body(configurationRESTManagementService.provideAllExistingDefaultConfigurationValues())
                );
    }

}
