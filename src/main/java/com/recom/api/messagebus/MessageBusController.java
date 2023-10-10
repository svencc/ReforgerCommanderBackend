package com.recom.api.messagebus;

import com.recom.api.commons.HttpCommons;
import com.recom.dto.message.MessageBusRequestDto;
import com.recom.dto.message.MessageBusResponseDto;
import com.recom.persistence.message.MessagePersistenceLayer;
import com.recom.service.AssertionService;
import com.recom.service.ReforgerPayloadParserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "MessageBus")
@RequestMapping("/api/v1/message-bus")
public class MessageBusController {

    private final static Duration RECOM_CURL_TIMEOUT = Duration.ofSeconds(60); // 12 seconds seems to be the maximum on REFORGER side!

    @NonNull
    private final AssertionService assertionService;
    @NonNull
    private final ReforgerPayloadParserService payloadParser;
    @NonNull
    private final MessagePersistenceLayer messagePersistenceLayer;

    @Operation(
            summary = "Get a list of messages",
            description = "Gets all map specific, latest message of a type.",
            security = @SecurityRequirement(name = HttpCommons.BEARER_AUTHENTICATION_REQUIREMENT)
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = HttpCommons.OK_CODE, description = HttpCommons.OK),
            @ApiResponse(responseCode = HttpCommons.UNAUTHORIZED_CODE, description = HttpCommons.UNAUTHORIZED, content = @Content())
    })
    @PostMapping(path = "/form", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<MessageBusResponseDto> getMessagesForm(
            @RequestParam(required = true)
            @NonNull final Map<String, String> payload
    ) {
        log.debug("Requested POST /api/v1/map/renderer/form (FORM)");

        return getMessagesJSON(payloadParser.parseValidated(payload, MessageBusRequestDto.class));
    }

    @SneakyThrows


    @Operation(
            summary = "Get a list of messages",
            description = "Gets all map specific, latest message of a type.",
            security = @SecurityRequirement(name = HttpCommons.BEARER_AUTHENTICATION_REQUIREMENT)
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = HttpCommons.OK_CODE, description = HttpCommons.OK),
            @ApiResponse(responseCode = HttpCommons.UNAUTHORIZED_CODE, description = HttpCommons.UNAUTHORIZED, content = @Content())
    })
    @PostMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageBusResponseDto> getMessagesJSON(
            @RequestBody(required = true)
            @NonNull @Valid final MessageBusRequestDto mapRendererRequestDto
    ) {
        log.debug("Requested POST /api/v1/map/message-bus (JSON)");

        assertionService.assertMapExists(mapRendererRequestDto.getMapName());
        log.debug("...");

        Thread.sleep(RECOM_CURL_TIMEOUT);

        if (false) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .cacheControl(CacheControl.noCache())
                    .build();
        } else {
            return ResponseEntity.status(HttpStatus.OK)
                    .cacheControl(CacheControl.noCache())
                    .body(
                            MessageBusResponseDto.builder()
                                    .messages(messagePersistenceLayer.findAllMapSpecificMessages(mapRendererRequestDto.getMapName()))
                                    .build()
                    );
        }
    }

}
