package com.recom.api.configuration;

import com.recom.api.commons.HttpCommons;
import com.recom.service.AssertionService;
import com.recom.service.configuration.ConfigurationDescriptorProvider;
import com.recom.service.configuration.ConfigurationMapToolsService;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "Configuration")
@RequestMapping("/api/v1/configuration/map-tools")
public class MapToolsController {

    @NonNull
    private final AssertionService assertionService;
    @NonNull
    private final ConfigurationMapToolsService configurationMapToolsService;


    @Operation(
            summary = "Set resources for clustering towns.",
            description = "Sets map specific town resources. REGEX pattern matching is explicitly supported!"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = HttpCommons.OK_CODE, description = HttpCommons.OK)
    })
    @PutMapping(path = "/resources/village", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> addVillageResources(
            @RequestParam
            @NonNull final String mapName,

            @RequestBody(required = false)
            @NonNull final List<String> entityMatcherList
    ) {
        log.debug("Requested POST /api/v1/configuration/map-tools/resources/village");
        assertionService.assertMapExists(mapName);

        return ResponseEntity.status(HttpStatus.OK)
                .cacheControl(CacheControl.noCache())
                .body(configurationMapToolsService.addResources(mapName, entityMatcherList, ConfigurationDescriptorProvider.CLUSTERING_VILLAGE_RESOURCES_LIST));
    }

    @Operation(
            summary = "Set resources for clustering towns.",
            description = "Sets map specific town resources. REGEX pattern matching is explicitly supported!"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = HttpCommons.OK_CODE, description = HttpCommons.OK)
    })
    @DeleteMapping(path = "/resources/village", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> removeVillageResources(
            @RequestParam
            @NonNull final String mapName,

            @RequestBody(required = false)
            @NonNull final List<String> entityMatcherList
    ) {
        log.debug("Requested DELETE /api/v1/configuration/map-tools/resources/village");
        assertionService.assertMapExists(mapName);

        return ResponseEntity.status(HttpStatus.OK)
                .cacheControl(CacheControl.noCache())
                .body(configurationMapToolsService.removeResources(mapName, entityMatcherList, ConfigurationDescriptorProvider.CLUSTERING_VILLAGE_RESOURCES_LIST));
    }

}
