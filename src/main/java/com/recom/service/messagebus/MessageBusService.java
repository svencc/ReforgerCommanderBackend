package com.recom.service.messagebus;

import com.recom.dto.message.MessageBusResponseDto;
import com.recom.dto.message.MessageDto;
import com.recom.mapper.MessageMapper;
import com.recom.model.message.MessageContainer;
import com.recom.observer.*;
import com.recom.persistence.message.MessagePersistenceLayer;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageBusService implements HasSubject<MessageContainer> {

    @NonNull
    @Getter()
    private final Subjective<MessageContainer> subject = new Subject<>();
    @NonNull
    private final MessagePersistenceLayer messagePersistenceLayer;

    public <T> void sendMessage(
            @NonNull final String mapName,
            @NonNull final MessageContainer messageContainer
    ) {
        subject.notifyObserversWith(new Notification<>(messageContainer));
    }

    @NonNull
    public MessageBusResponseDto listMessagesSince(
            @NonNull final String mapName,
            @NonNull final Long sinceTimestampEpochMilliseconds

    ) {
        return MessageBusResponseDto.builder()
                .mapName(mapName)
                .messages(messagePersistenceLayer.findAllMapSpecificMessagesSince(mapName, sinceTimestampEpochMilliseconds))
                .build();
    }

}
