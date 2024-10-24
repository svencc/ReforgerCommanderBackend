package com.recom.api.messagebus;

import com.recom.api.commons.HttpCommons;
import com.recom.dto.message.MessageBusLongPollRequestDto;
import com.recom.dto.message.MessageBusResponseDto;
import com.recom.entity.map.GameMap;
import com.recom.service.AssertionService;
import com.recom.service.ReforgerPayloadParserService;
import com.recom.service.messagebus.MessageBusService;
import com.recom.service.messagebus.MessageLongPollObserver;
import com.recom.util.StringUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Lazy;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "MessageBus")
@RequestMapping("/api/v1/message-bus")
public class MessageBusController {

    private final static Duration RECOM_CURL_TIMEOUT = Duration.ofSeconds(13); // 12 seconds seems to be the maximum on REFORGER side!

    @NonNull
    private final AssertionService assertionService;
    @NonNull
    private final ReforgerPayloadParserService payloadParser;
    @NonNull
    private final MessageBusService messageBusService;

    @Operation(
            summary = "Long-poll latest messages",
            description = "Polls for all com.recom.dto.map specific, latest com.recom.dto.message.",
            security = @SecurityRequirement(name = HttpCommons.BEARER_AUTHENTICATION_REQUIREMENT)
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = HttpCommons.OK_CODE, description = HttpCommons.OK, content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageBusResponseDto.class))),
            @ApiResponse(responseCode = HttpCommons.UNAUTHORIZED_CODE, description = HttpCommons.UNAUTHORIZED, content = @Content())
    })
    @PostMapping(path = "/form", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<ResponseBodyEmitter> messageBusFORM(
            @RequestParam(required = true)
            @NonNull final Map<String, String> payload
    ) {
        log.debug("Requested POST /api/v1/com.recom.dto.message-bus/form (FORM)");

        return messageBusJSON(payloadParser.parseValidated(payload, MessageBusLongPollRequestDto.class));
    }

    @Operation(
            summary = "Long-poll latest messages",
            description = "Polls for map specific, latest messages.",
            security = @SecurityRequirement(name = HttpCommons.BEARER_AUTHENTICATION_REQUIREMENT)
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = HttpCommons.OK_CODE, description = HttpCommons.OK, content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageBusResponseDto.class))),
            @ApiResponse(responseCode = HttpCommons.UNAUTHORIZED_CODE, description = HttpCommons.UNAUTHORIZED, content = @Content())
    })
    @PostMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseBodyEmitter> messageBusJSON(
            @RequestBody(required = true)
            @NonNull @Valid final MessageBusLongPollRequestDto messageBusLongPollRequestDto
    ) {
        if (StringUtil.isNumeric(messageBusLongPollRequestDto.getSinceEpochMilliseconds())) {
            log.debug("Requested POST /api/v1/com.recom.dto.message-bus since {} (JSON)", messageBusLongPollRequestDto.getSinceEpochMilliseconds());
        } else {
            log.debug("Requested POST /api/v1/com.recom.dto.message-bus (JSON)");
        }
        final GameMap gameMap = assertionService.provideMapOrExitWith404(messageBusLongPollRequestDto.getMapName());

        final ResponseBodyEmitter emitter;
        final Long sinceEpochMillis = Optional.ofNullable(messageBusLongPollRequestDto.getSinceEpochMilliseconds())
                .filter(epochStringValue -> !epochStringValue.isBlank())
                .map(Long::valueOf)
                .orElse(null);

        final Lazy<MessageBusResponseDto> messagesSinceLazy = Lazy.of(() -> messageBusService.listMessagesSince(gameMap, sinceEpochMillis));
        if (StringUtil.isNumeric(messageBusLongPollRequestDto.getSinceEpochMilliseconds()) && !messagesSinceLazy.get().getMessages().isEmpty()) {
            emitter = new ResponseBodyEmitter(RECOM_CURL_TIMEOUT.toMillis());
            try {
                log.debug("Sending messages since {} to client: {}", messageBusLongPollRequestDto.getSinceEpochMilliseconds(), messagesSinceLazy.get());
                emitter.send(messagesSinceLazy.get(), MediaType.APPLICATION_JSON);
                emitter.complete();
            } catch (final Exception e) {
                log.error("MessageBusController.getMessagesJSON: ", e);
                emitter.completeWithError(e);
            }
        } else {
            final MessageLongPollObserver messageLongPollObserver = new MessageLongPollObserver(RECOM_CURL_TIMEOUT.toMillis());
            messageLongPollObserver.observe(messageBusService.getSubject());
            emitter = messageLongPollObserver.provideResponseEmitter();
        }

        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return ResponseEntity.status(HttpStatus.OK)
                .headers(httpHeaders)
                .cacheControl(CacheControl.noCache())
                .body(emitter);
    }

}