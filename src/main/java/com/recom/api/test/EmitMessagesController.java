package com.recom.api.test;

import com.recom.api.commons.HttpCommons;
import com.recom.configuration.AsyncConfiguration;
import com.recom.dto.message.MessageBusLongPollRequestDto;
import com.recom.model.message.MessageContainer;
import com.recom.model.message.MessageType;
import com.recom.model.message.OneMessage;
import com.recom.service.AssertionService;
import com.recom.service.messagebus.MessageBusService;
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
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "MessageBus")
@RequestMapping("/api/v1/test/emit-messages")
public class EmitMessagesController {

    @NonNull
    private final AssertionService assertionService;
    @NonNull
    private final MessageBusService messageBusService;
    @NonNull
    private final AsyncConfiguration asyncConfiguration;

    @Operation(
            summary = "Test-Only: Emits Messages",
            description = "Push message over message-bus.",
            security = @SecurityRequirement(name = HttpCommons.BEARER_AUTHENTICATION_REQUIREMENT)
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = HttpCommons.OK_CODE, description = HttpCommons.OK),
            @ApiResponse(responseCode = HttpCommons.UNAUTHORIZED_CODE, description = HttpCommons.UNAUTHORIZED, content = @Content())
    })
    @PostMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> emitMessagesJSON(
            @RequestBody(required = true)
            @NonNull @Valid final MessageBusLongPollRequestDto messageBusLongPollRequestDto
    ) {
        log.debug("Requested POST /api/v1/test/emit-messages (JSON)");
        assertionService.assertMapExists(messageBusLongPollRequestDto.getMapName());

        messageBusService.sendMessage(MessageContainer.builder()
                .mapName(messageBusLongPollRequestDto.getMapName())
                .messages(List.of(
                        OneMessage.builder()
                                .messageType(MessageType.FETCH_MAP_RENDER_DATA)
                                .payload(null)
                                .build(),
                        OneMessage.builder()
                                .messageType(MessageType.FETCH_MAP_RENDER_DATA)
                                .payload(null)
                                .build()
                ))
                .build()
        );

        return ResponseEntity.status(HttpStatus.OK)
                .cacheControl(CacheControl.noCache())
                .body(null);
    }

}
