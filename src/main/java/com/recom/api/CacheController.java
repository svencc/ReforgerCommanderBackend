package com.recom.api;

import com.recom.event.event.sync.cache.CacheResetSyncEvent;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Cache")
@RequestMapping("/api/v1/cache")
public class CacheController {

    @NonNull
    private final ApplicationEventPublisher applicationEventPublisher;

    @Operation(
            summary = "Deletes the complete application cache",
            description = "Delete cache. Everything"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "O.K.")
    })
    @DeleteMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> getJsonTestData() {
        log.info("Requested DELETE /api/v1/cache");

        applicationEventPublisher.publishEvent(new CacheResetSyncEvent());

        return ResponseEntity.status(HttpStatus.OK)
                .cacheControl(CacheControl.noCache())
                .build();
    }

}