package com.recom.api.map;

import com.recom.api.commons.HttpCommons;
import com.recom.dto.map.topography.MapTopographyRequestDto;
import com.recom.exception.HttpNotFoundException;
import com.recom.security.account.RECOMAccount;
import com.recom.security.account.RECOMAuthorities;
import com.recom.service.AssertionService;
import com.recom.service.map.TopographyMapDataService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@Tag(name = "Maps")
@RequiredArgsConstructor
@RequestMapping("/api/v1/map/topography")
public class MapTopographyController {

    @NonNull
    private final TopographyMapDataService topographyMapDataService;
    @NonNull
    private final AssertionService assertionService;


    @Operation(
            summary = "Gets map topography",
            description = "Return a list of measured height points and some meta.",
            security = @SecurityRequirement(name = HttpCommons.BEARER_AUTHENTICATION_REQUIREMENT)
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = HttpCommons.OK_CODE, description = HttpCommons.OK),
            @ApiResponse(responseCode = HttpCommons.UNAUTHORIZED_CODE, description = HttpCommons.UNAUTHORIZED, content = @Content())
    })
    @PostMapping(path = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.IMAGE_PNG_VALUE)
    @Secured({RECOMAuthorities.EVERYBODY})
    public ResponseEntity<byte[]> listMapNames(
            @AuthenticationPrincipal RECOMAccount recomAccount,
            @RequestBody final MapTopographyRequestDto mapTopographyRequestDto
    ) throws IOException {
        log.debug("Requested GET /api/v1/map/topography");

        try {
            assertionService.assertMapExists(mapTopographyRequestDto.getMapName());
            return ResponseEntity.status(HttpStatus.OK)
                    .cacheControl(CacheControl.noCache())
                    .body(topographyMapDataService.provideTopographyMap(mapTopographyRequestDto.getMapName()));
        } catch (final HttpNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .cacheControl(CacheControl.noCache())
                    .build();
        }
    }

}
