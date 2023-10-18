package com.recom.service.messagebus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.recom.configuration.AsyncConfiguration;
import com.recom.dto.map.Point2DDto;
import com.recom.dto.message.MessageBusResponseDto;
import com.recom.dto.message.MessageDto;
import com.recom.entity.Message;
import com.recom.exception.HttpTimeoutException;
import com.recom.model.message.MessageType;
import com.recom.model.message.OneMessage;
import com.recom.model.message.MessageContainer;
import com.recom.observer.Notification;
import com.recom.observer.ObserverTemplate;
import com.recom.observer.Subjective;
import com.recom.persistence.message.MessagePersistenceLayer;
import com.recom.service.provider.StaticObjectMapperProvider;
import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
public class MessageLongPollObserver extends ObserverTemplate<MessageContainer> {

    @NonNull
    private final Long timeout;
    @NonNull
    private final AsyncTaskExecutor asyncTaskExecutor;
    @NonNull
    private final MessagePersistenceLayer messagePersistenceLayer;
    @NonNull
    private final ResponseBodyEmitter responseBodyEmitter;

    @Builder()
    public MessageLongPollObserver(
            @NonNull final Long timeout,
            @NonNull final AsyncTaskExecutor asyncTaskExecutor,
            @NonNull final MessagePersistenceLayer messagePersistenceLayer
    ) {
        this.timeout = timeout;
        this.asyncTaskExecutor = asyncTaskExecutor;
        this.messagePersistenceLayer = messagePersistenceLayer;

        this.responseBodyEmitter = new ResponseBodyEmitter(timeout);
        this.responseBodyEmitter.onTimeout(() -> {
            log.debug("MessageLongPollObserver.onTimeout");
            super.subjects.forEach(subject -> subject.observationStoppedThrough(this));
            this.responseBodyEmitter.completeWithError(new HttpTimeoutException("Timeout"));
        });
    }

    @Override
    public void takeNotice(
            @NonNull final Subjective<MessageContainer> subject,
            @NonNull final Notification<MessageContainer> messageBag
    ) {
        log.debug("MessageLongPollObserver.takeNotice");
        try {
            final MessageBusResponseDto response = prepareNotification(messageBag.getPayload());
            responseBodyEmitter.send(response, MediaType.APPLICATION_JSON);
            persistNotification(response);
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
            responseBodyEmitter.completeWithError(e);
        } finally {
            subject.observationStoppedThrough(this);
            responseBodyEmitter.complete();
        }
    }

    @NonNull
    private MessageBusResponseDto prepareNotification(@NonNull final MessageContainer container) {
        final Instant now = Instant.now();
        long epochMilli = now.toEpochMilli();
        return MessageBusResponseDto.builder()
                .mapName(container.getMapName())
                .messages(container.getMessages().stream()
                        .map(message -> {
                            return MessageDto.builder()
                                    .uuid(UUID.randomUUID())
                                    .messageType(message.getMessageType())
                                    .payload(message.getPayload())
                                    .timestampEpochMilliseconds(epochMilli)
                                    .build();
                        })
                        .toList()
                )
                .build();
    }

    private void persistNotification(@NonNull final MessageBusResponseDto messageBusResponse) {
        final List<Message> messagesToSave = messageBusResponse.getMessages().stream()
                .map(message -> {
                            try {
                                return Message.builder()
                                        .uuid(message.getUuid())
                                        .mapName(messageBusResponse.getMapName())
                                        .messageType(message.getMessageType())
                                        .payload(StaticObjectMapperProvider.provide().writeValueAsString(message.getPayload()))
                                        .timestamp(LocalDateTime.ofInstant(Instant.ofEpochMilli(message.getTimestampEpochMilliseconds()), ZoneId.systemDefault()))
                                        .build();
                            } catch (final JsonProcessingException jpe) {
                                log.error("JsonProcessingException: ", jpe);
                            }
                            return null;
                        }

                )
                .filter(Objects::nonNull)
                .toList();

        if (!messagesToSave.isEmpty()) {
            messagePersistenceLayer.saveAll(messagesToSave);
        }
    }

    @NonNull
    public ResponseBodyEmitter provideResponseEmitter() {
        return responseBodyEmitter;
    }

    public void scheduleTestResponse(
            @NonNull final String mapName,
            @NonNull final Duration duration,
            @NonNull final Subjective<MessageContainer> onBehalfOf,
            @NonNull final AsyncConfiguration asyncConfiguration
    ) {
        log.debug("MessageLongPollObserver.startObserving");
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(duration.toMillis());
            } catch (final InterruptedException ignored) {
                log.error("Interrupted Exception");
            }
            takeNotice(
                    onBehalfOf,
                    new Notification<>(
                            MessageContainer.builder()
                                    .mapName(mapName)
                                    .messages(List.of(
                                                    OneMessage.builder()
                                                            .messageType(MessageType.TEST)
                                                            .payload(Point2DDto.builder()
                                                                    .x(BigDecimal.valueOf(1.0))
                                                                    .y(BigDecimal.valueOf(2.0))
                                                                    .build())
                                                            .build(),
                                                    OneMessage.builder()
                                                            .messageType(MessageType.TEST)
                                                            .payload(Point2DDto.builder()
                                                                    .x(BigDecimal.valueOf(1.0))
                                                                    .y(BigDecimal.valueOf(2.0))
                                                                    .build())
                                                            .build()
                                            )
                                    )
                                    .build()
                    )
            );
        }, asyncConfiguration.provideVirtualThreadPerTaskExecutor());
    }

}
