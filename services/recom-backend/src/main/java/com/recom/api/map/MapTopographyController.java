package com.recom.api.map;

import com.recom.api.commons.HttpCommons;
import com.recom.commons.model.DEMDescriptor;
import com.recom.dto.map.topography.HeightMapDescriptorDto;
import com.recom.dto.map.topography.MapTopographyRequestDto;
import com.recom.entity.map.GameMap;
import com.recom.exception.HttpNotFoundException;
import com.recom.mapper.HeightMapDescriptorMapper;
import com.recom.security.account.RECOMAccount;
import com.recom.security.account.RECOMAuthorities;
import com.recom.service.AssertionService;
import com.recom.service.map.topography.MapService;
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
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@Tag(name = "Maps")
@RequiredArgsConstructor
@RequestMapping("/api/v1/map/topography")
public class MapTopographyController {

    @NonNull
    private final MapService mapService;
    @NonNull
    private final AssertionService assertionService;


    @Operation(
            summary = "Generates topography com.recom.dto.map",
            description = "Return an topography com.recom.dto.map image.",
            security = @SecurityRequirement(name = HttpCommons.BEARER_AUTHENTICATION_REQUIREMENT)
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = HttpCommons.OK_CODE, description = HttpCommons.OK),
            @ApiResponse(responseCode = HttpCommons.UNAUTHORIZED_CODE, description = HttpCommons.UNAUTHORIZED, content = @Content())
    })
    @PostMapping(path = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.IMAGE_PNG_VALUE)
    @Secured({RECOMAuthorities.EVERYBODY})
    public ResponseEntity<byte[]> generateTopographyMap(
            @AuthenticationPrincipal RECOMAccount recomAccount,
            @RequestBody final MapTopographyRequestDto mapTopographyRequestDto
    ) {
        log.debug("Requested GET /api/v1/com.recom.dto.map/topography");

        try {
            final GameMap gameMap = assertionService.provideMap(mapTopographyRequestDto.getMapName());
            return ResponseEntity.status(HttpStatus.OK)
                    .cacheControl(CacheControl.noCache())
                    .body(mapService.provideHeightMapPNG(gameMap));
        } catch (final HttpNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .cacheControl(CacheControl.noCache())
                    .build();
        }
    }

    @Operation(
            summary = "Gets com.recom.dto.map topography",
            description = "Return a list of measured height points and some meta.",
            security = @SecurityRequirement(name = HttpCommons.BEARER_AUTHENTICATION_REQUIREMENT)
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = HttpCommons.OK_CODE, description = HttpCommons.OK),
            @ApiResponse(responseCode = HttpCommons.UNAUTHORIZED_CODE, description = HttpCommons.UNAUTHORIZED, content = @Content())
    })
    @PostMapping(path = "/data", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({RECOMAuthorities.EVERYBODY})
    public ResponseEntity<HeightMapDescriptorDto> getTopographyMapData(
            @AuthenticationPrincipal RECOMAccount recomAccount,
            @RequestBody final MapTopographyRequestDto mapTopographyRequestDto
    ) {
        log.debug("Requested GET /api/v1/com.recom.dto.map/topography/data");

        final GameMap gameMap = assertionService.provideMap(mapTopographyRequestDto.getMapName());
        final DEMDescriptor command = mapService.provideDEMDescriptor(gameMap)
                .orElseThrow(()-> new HttpNotFoundException("No topography com.recom.dto.map found for com.recom.dto.map with id " + gameMap.getId() + "!"));

        return ResponseEntity.status(HttpStatus.OK)
                .cacheControl(CacheControl.noCache())
                .body(HeightMapDescriptorMapper.INSTANCE.toDto(command, gameMap.getName()));
    }

}
