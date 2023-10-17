package com.recom.service.messagebus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.recom.configuration.AsyncConfiguration;
import com.recom.dto.message.MessageBusResponseDto;
import com.recom.dto.message.MessageDto;
import com.recom.entity.Message;
import com.recom.exception.HttpTimeoutException;
import com.recom.model.message.MessageType;
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

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
public class MessageLongPollObserver extends ObserverTemplate<MessageBusResponseDto> {

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
            @NonNull final Subjective<MessageBusResponseDto> subject,
            @NonNull final Notification<MessageBusResponseDto> notification
    ) {
        log.debug("MessageLongPollObserver.takeNotice");
        try {
            responseBodyEmitter.send(notification.getPayload(), MediaType.APPLICATION_JSON);

            persistNotification(notification);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            responseBodyEmitter.completeWithError(e);
        } finally {
            subject.observationStoppedThrough(this);
            responseBodyEmitter.complete();
        }
    }

    private void persistNotification(@NonNull final Notification<MessageBusResponseDto> notification) {
        final List<Message> messagesToSave = notification.getPayload().getMessages().stream()
                .map(messageDto -> {
                    try {
                        return Message.builder()
                                .mapName(notification.getPayload().getMapName())
                                .messageType(MessageType.TEST)
                                .payload(StaticObjectMapperProvider.provide().writeValueAsString(messageDto))
                                .timestamp(LocalDateTime.now())
                                .build();
                    } catch (JsonProcessingException jpe) {
                        log.error("JsonProcessingException: ", jpe);
                    }

                    return null;
                })
                .filter(Objects::nonNull)
                .toList();

        messagePersistenceLayer.saveAll(messagesToSave);
    }

    @NonNull
    public ResponseBodyEmitter provideResponseEmitter() {
        return responseBodyEmitter;
    }

    public void scheduleTestResponse(
            @NonNull final String mapName,
            @NonNull final Duration duration,
            @NonNull final Subjective<MessageBusResponseDto> onBehalfOf,
            @NonNull final AsyncConfiguration asyncConfiguration
    ) {
        log.debug("MessageLongPollObserver.startObserving");
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(duration.toMillis());
            } catch (InterruptedException ignored) {
                log.error("Interrupted Exception");
            }
            takeNotice(
                    onBehalfOf,
                    new Notification<>(
                            MessageBusResponseDto.builder()
                                    .mapName(mapName)
                                    .messages(List.of(MessageDto.builder().uuid(UUID.randomUUID()).build()))
                                    .build()
                    )
            );
        }, asyncConfiguration.provideVirtualThreadPerTaskExecutor());
    }

}
