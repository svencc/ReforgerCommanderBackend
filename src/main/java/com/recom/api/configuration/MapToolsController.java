package com.recom.api.configuration;

import com.recom.api.commons.HttpCommons;
import com.recom.dto.configuration.post.OverrideConfigurationDto;
import com.recom.event.event.async.cache.CacheResetAsyncEvent;
import com.recom.service.configuration.ConfigurationDescriptorProvider;
import com.recom.service.configuration.ConfigurationRESTManagementService;
import com.recom.service.configuration.ConfigurationValueProvider;
import com.recom.service.AssertionService;
import com.recom.service.map.MapMetaDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

import java.util.ArrayList;
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
    private final MapMetaDataService mapMetaDataService;
    @NonNull
    private final ConfigurationRESTManagementService configurationRESTManagementService;
    @NonNull
    private final ConfigurationValueProvider configurationValueProvider;
    @NonNull
    private final ApplicationEventPublisher applicationEventPublisher;

    @Operation(
            summary = "Set entities for clustering towns.",
            description = "Sets map specific town entities. REGEX matching is also supported!"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = HttpCommons.OK_CODE, description = HttpCommons.OK)
    })
    @PostMapping(path = "/town/entities", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> postTownEntities(
            @RequestParam
            @NonNull final String mapName,

            @RequestBody(required = false)
            @NonNull final List<String> entityMatcherList
    ) {
        log.debug("Requested POST /api/v1/map/configuration/map-tools/town/entities");

        assertionService.assertMapExists(mapName);


        // take entityMatcherList and create a list of OverrideConfigurationDto
        final List<String> entitiesToAdd = new ArrayList<>();
        entityMatcherList.forEach(entityMatcher -> {
            final List<String> matchedEntities = mapMetaDataService.provideMapMeta(mapName).getUtilizedClasses().stream()
                    .filter(utilizedClass -> utilizedClass.matches(entityMatcher))
                    .toList();

            entitiesToAdd.addAll(matchedEntities);
        });

        final List<String> distinctEntitiesToAdd = entitiesToAdd.stream()
                .distinct()
                .sorted()
                .toList();

        // read conf
        final List<String> existingTownClusterEntities = configurationValueProvider.queryValue(mapName, ConfigurationDescriptorProvider.TEST_JSON_LIST);


        // merge with distinctEntitiesToAdd
        // sort
        // save/update

        configurationRESTManagementService.updateOverride(
                mapName,
                OverrideConfigurationDto.builder()
                        .namespace(ConfigurationDescriptorProvider.TEST_JSON_LIST.getNamespace())
                        .name(ConfigurationDescriptorProvider.TEST_JSON_LIST.getName())
                        .type(ConfigurationDescriptorProvider.TEST_JSON_LIST.getType())
//                        .mapOverrideListValue(entityList)
                        .build()
        );
        applicationEventPublisher.publishEvent(new CacheResetAsyncEvent());

        return ResponseEntity.status(HttpStatus.OK)
                .cacheControl(CacheControl.noCache())
                .build();
    }
}
