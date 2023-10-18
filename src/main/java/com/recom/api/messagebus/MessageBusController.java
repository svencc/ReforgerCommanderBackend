package com.recom.api.messagebus;

import com.recom.api.commons.HttpCommons;
import com.recom.configuration.AsyncConfiguration;
import com.recom.dto.message.MessageBusLongPollRequestDto;
import com.recom.dto.message.MessageBusResponseDto;
import com.recom.dto.message.MessageBusSinceRequestDto;
import com.recom.persistence.message.MessagePersistenceLayer;
import com.recom.service.AssertionService;
import com.recom.service.ReforgerPayloadParserService;
import com.recom.service.messagebus.MessageBusService;
import com.recom.service.messagebus.MessageLongPollObserver;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.time.Duration;
import java.util.Map;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "MessageBus")
@RequestMapping("/api/v1/message-bus")
public class MessageBusController {

    private final static Duration RECOM_CURL_TIMEOUT = Duration.ofSeconds(5); // 12 seconds seems to be the maximum on REFORGER side!

    @NonNull
    private final AssertionService assertionService;
    @NonNull
    private final ReforgerPayloadParserService payloadParser;
    @NonNull
    private final MessagePersistenceLayer messagePersistenceLayer;
    @NonNull
    private final MessageBusService messageBusService;
    @NonNull
    private final AsyncConfiguration asyncConfiguration;

    @Operation(
            summary = "Long-poll latest messages",
            description = "Polls for all map specific, latest message.",
            security = @SecurityRequirement(name = HttpCommons.BEARER_AUTHENTICATION_REQUIREMENT)
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = HttpCommons.OK_CODE, description = HttpCommons.OK),
            @ApiResponse(responseCode = HttpCommons.UNAUTHORIZED_CODE, description = HttpCommons.UNAUTHORIZED, content = @Content())
    })
    @PostMapping(path = "/form", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<ResponseBodyEmitter> getMessagesForm(
            @RequestParam(required = true)
            @NonNull final Map<String, String> payload
    ) {
        log.debug("Requested POST /api/v1/map/renderer/form (FORM)");

        return getMessagesJSON(payloadParser.parseValidated(payload, MessageBusLongPollRequestDto.class));
    }

    @Operation(
            summary = "Long-poll latest messages",
            description = "Polls for all map specific, latest message.",
            security = @SecurityRequirement(name = HttpCommons.BEARER_AUTHENTICATION_REQUIREMENT)
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = HttpCommons.OK_CODE, description = HttpCommons.OK),
            @ApiResponse(responseCode = HttpCommons.UNAUTHORIZED_CODE, description = HttpCommons.UNAUTHORIZED, content = @Content())
    })
    @PostMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseBodyEmitter> getMessagesJSON(
            @RequestBody(required = true)
            @NonNull @Valid final MessageBusLongPollRequestDto messageBusLongPollRequestDto
    ) {
        log.debug("Requested POST /api/v1/map/message-bus (JSON)");
        assertionService.assertMapExists(messageBusLongPollRequestDto.getMapName());

        final MessageLongPollObserver messageLongPollObserver = MessageLongPollObserver.builder()
                .timeout(RECOM_CURL_TIMEOUT.toMillis())
                .asyncTaskExecutor(asyncConfiguration.provideClusterGeneratorExecutor())
                .messagePersistenceLayer(messagePersistenceLayer)
                .build();
        messageLongPollObserver.observe(messageBusService.getSubject());

        messageLongPollObserver.scheduleTestResponse(messageBusLongPollRequestDto.getMapName(), Duration.ofSeconds(5), messageBusService.getSubject(), asyncConfiguration);

        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        return ResponseEntity.status(HttpStatus.OK)
                .headers(httpHeaders)
                .cacheControl(CacheControl.noCache())
                .body(messageLongPollObserver.provideResponseEmitter());
    }

    @Operation(
            summary = "Get a list of messages since given timestamp",
            description = "Gets all map specific message since provided epoch-millis.",
            security = @SecurityRequirement(name = HttpCommons.BEARER_AUTHENTICATION_REQUIREMENT)
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = HttpCommons.OK_CODE, description = HttpCommons.OK),
            @ApiResponse(responseCode = HttpCommons.UNAUTHORIZED_CODE, description = HttpCommons.UNAUTHORIZED, content = @Content())
    })
    @PostMapping(path = "/after", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageBusResponseDto> getMessagesSinceJSON(
            @RequestBody(required = true)
            @NonNull @Valid final MessageBusSinceRequestDto messageBusSinceRequestDto
    ) {
        log.debug("Requested POST /api/v1/map/message-bus/after (JSON)");
        assertionService.assertMapExists(messageBusSinceRequestDto.getMapName());

        return ResponseEntity.status(HttpStatus.OK)
                .cacheControl(CacheControl.noCache())
                .body(messageBusService.listMessagesSince(messageBusSinceRequestDto.getMapName(), messageBusSinceRequestDto.getTimestampEpochMilliseconds()));
    }

}
