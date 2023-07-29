package com.recom.api.map;

import com.recom.api.commons.HttpCommons;
import com.recom.security.user.RECOMUser;
import com.recom.service.map.MapMetaDataService;
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
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@Tag(name = "Maps")
@RequiredArgsConstructor
@RequestMapping("/api/v1/maps")
public class MapsController {

    @NonNull
    private final MapMetaDataService mapMetaDataService;


    @Operation(
            summary = "Gets a list of all scanned map names.",
            description = "Return a list of map names."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = HttpCommons.OK_CODE, description = HttpCommons.OK)
    })
    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({"AUTHORITY_TEST"})
    public ResponseEntity<List<String>> listMapNames(
            @AuthenticationPrincipal RECOMUser recomUser
    ) {
        log.debug("Requested GET /api/v1/maps");

        return ResponseEntity.status(HttpStatus.OK)
                .cacheControl(CacheControl.noCache())
                .body(mapMetaDataService.provideAllMapNames());
    }

}
