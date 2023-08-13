package com.recom.api;

import com.recom.api.commons.HttpCommons;
import com.recom.dto.EpochTimeDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Time")
@RequestMapping("/api/v1/time")
public class TimeController {

    @Operation(
            summary = "Get Unix-Epoche time in seconds",
            description = "Just returns the current time in seconds since 1970-01-01 00:00:00 UTC.",
            security = @SecurityRequirement(name = HttpCommons.BEARER_AUTHENTICATION_REQUIREMENT)
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = HttpCommons.OK_CODE, description = HttpCommons.OK)
    })
    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EpochTimeDto> epochTime() {
        log.debug("Requested GET /api/v1/time");

        final Instant now = Instant.now();
        return ResponseEntity
                .status(HttpStatus.OK)
                .cacheControl(CacheControl.noCache())
                .body(EpochTimeDto.builder()
                        .epochSeconds(now.getEpochSecond())
                        .epochMilliseconds(now.toEpochMilli())
                        .build()
                );
    }

}
