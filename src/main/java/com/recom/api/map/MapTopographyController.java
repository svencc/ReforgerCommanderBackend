package com.recom.api.map;

import com.recom.api.commons.HttpCommons;
import com.recom.dto.map.topography.MapTopographyRequestDto;
import com.recom.security.account.RECOMAccount;
import com.recom.security.account.RECOMAuthorities;
import com.recom.service.map.MapMetaDataService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@Tag(name = "Maps")
@RequiredArgsConstructor
@RequestMapping("/api/v1/map/topography")
public class MapTopographyController {

    @NonNull
    private final MapMetaDataService mapMetaDataService;


    @Operation(
            summary = "Gets map topography",
            description = "Return a list of measured height points and some meta.",
            security = @SecurityRequirement(name = HttpCommons.BEARER_AUTHENTICATION_REQUIREMENT)
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = HttpCommons.OK_CODE, description = HttpCommons.OK),
            @ApiResponse(responseCode = HttpCommons.UNAUTHORIZED_CODE, description = HttpCommons.UNAUTHORIZED, content = @Content())
    })
    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({RECOMAuthorities.EVERYBODY})
    public ResponseEntity<List<String>> listMapNames(
            @AuthenticationPrincipal RECOMAccount recomAccount,
            @RequestBody final MapTopographyRequestDto mapTopographyRequestDto
    ) {
        log.debug("Requested GET /api/v1/maps");

        return ResponseEntity.status(HttpStatus.OK)
                .cacheControl(CacheControl.noCache())
                .body(mapMetaDataService.provideAllMapNames());
    }

}
