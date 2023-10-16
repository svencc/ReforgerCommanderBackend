package com.recom.service.messagebus;

import com.recom.dto.message.MessageBusResponseDto;
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

import java.io.IOException;

@Slf4j
@Builder
@RequiredArgsConstructor
public class MessageLongPollObserver extends ObserverTemplate<MessageBusResponseDto> {

    @NonNull
    private final Long timeout;
    @NonNull
    private final AsyncTaskExecutor asyncTaskExecutor;
    @NonNull
    private final ResponseBodyEmitter responseBodyEmitter;


    @Override
    public void takeNotice(
            @NonNull final Subjective<MessageBusResponseDto> subject,
            @NonNull final Notification<MessageBusResponseDto> notification
    ) {
        System.out.println("MessageLongPollObserver.takeNotice");
        try {
            responseBodyEmitter.send(notification.getPayload(), MediaType.APPLICATION_JSON);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            subject.observationStoppedThrough(this);
            responseBodyEmitter.complete();
        }
    }

}
