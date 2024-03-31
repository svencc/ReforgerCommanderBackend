package com.recom.service.messagebus;

import com.recom.dto.message.MessageBusResponseDto;
import com.recom.exception.HttpTimeoutException;
import com.recom.observer.Notification;
import com.recom.observer.ObserverTemplate;
import com.recom.observer.Subjective;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

@Slf4j
@RequiredArgsConstructor
public class MessageLongPollObserver extends ObserverTemplate<MessageBusResponseDto> {

    @NonNull
    private final ResponseBodyEmitter responseBodyEmitter;

    public MessageLongPollObserver(@NonNull final Long timeout) {
        this.responseBodyEmitter = new ResponseBodyEmitter(timeout);
        this.responseBodyEmitter.onTimeout(() -> {
            log.debug("MessageLongPollObserver.onTimeout");
            super.subjects.forEach(subject -> subject.observationStoppedThrough(this));
            this.responseBodyEmitter.completeWithError(new HttpTimeoutException("Timeout"));
        });
    }

    @Override
    @Synchronized
    public void takeNotice(
            @NonNull final Subjective<MessageBusResponseDto> subject,
            @NonNull final Notification<MessageBusResponseDto> response
    ) {
        try {
            responseBodyEmitter.send(response.getPayload(), MediaType.APPLICATION_JSON);
        } catch (final Exception e) {
            log.error("MessageLongPollObserver.takeNotice", e);
            responseBodyEmitter.completeWithError(e);
        } finally {
            responseBodyEmitter.complete();
            subject.observationStoppedThrough(this);
        }
    }

    @NonNull
    public ResponseBodyEmitter provideResponseEmitter() {
        return responseBodyEmitter;
    }

}
