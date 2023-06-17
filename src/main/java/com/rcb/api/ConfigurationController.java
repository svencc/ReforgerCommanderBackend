package com.rcb.api;

import com.rcb.api.commons.HttpCommons;
import com.rcb.dto.configuration.ConfigurationListDto;
import com.rcb.service.configuration.ConfigurationValueProvider;
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
    private final ConfigurationValueProvider configurationValueProvider;

    @Operation(
            summary = "Gets configuration data.",
            description = "Gets all or map specific configuration data, with value inheritance."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = HttpCommons.OK_CODE, description = HttpCommons.OK)
    })
    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ConfigurationListDto> getConfigurations(
            @RequestParam(required = false)
            @NonNull final Optional<String> mapNameOpt
    ) {
        log.debug("Requested GET /api/v1/map/configuration");

        return mapNameOpt.map((final String mapName) -> ResponseEntity.status(HttpStatus.OK)
                        .cacheControl(CacheControl.noCache())
                        .body(configurationValueProvider.provideAllExistingValueEntities(mapName)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.OK)
                        .cacheControl(CacheControl.noCache())
                        .body(configurationValueProvider.provideAllExistingValueEntities())
                );
    }

}
