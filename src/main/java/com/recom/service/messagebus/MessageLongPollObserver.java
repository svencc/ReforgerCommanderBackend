package com.recom.service.messagebus;

import com.recom.configuration.AsyncConfiguration;
import com.recom.dto.message.MessageBusResponseDto;
import com.recom.dto.message.MessageDto;
import com.recom.exception.HttpTimeoutException;
import com.recom.observer.Notification;
import com.recom.observer.ObserverTemplate;
import com.recom.observer.Subjective;
import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.time.Duration;
import java.util.List;
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
    private final ResponseBodyEmitter responseBodyEmitter;

    @Builder()
    public MessageLongPollObserver(
            @NonNull final Long timeout,
            @NonNull final AsyncTaskExecutor asyncTaskExecutor
    ) {
        this.timeout = timeout;
        this.asyncTaskExecutor = asyncTaskExecutor;
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
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            responseBodyEmitter.completeWithError(e);
        } finally {
            subject.observationStoppedThrough(this);
            responseBodyEmitter.complete();
        }
    }

    @NonNull
    public ResponseBodyEmitter provideResponseEmitter() {
        return responseBodyEmitter;
    }

    public void schedlueTestResponse(
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
                                    .messages(List.of(MessageDto.builder().uuid(UUID.randomUUID()).build()))
                                    .build()
                    )
            );
        }, asyncConfiguration.provideVirtualThreadPerTaskExecutor());
    }

}
