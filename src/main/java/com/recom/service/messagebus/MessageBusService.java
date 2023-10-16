package com.recom.service.messagebus;

import com.recom.dto.message.MessageBusResponseDto;
import com.recom.dto.message.MessageDto;
import com.recom.entity.Message;
import com.recom.model.message.MessageType;
import com.recom.observer.*;
import com.recom.persistence.message.MessagePersistenceLayer;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageBusService implements HasSubject<MessageBusResponseDto> {

    @NonNull
    @Getter()
    private final Subjective<MessageBusResponseDto> subject = new Subject<>();

    @NonNull
    private final MessagePersistenceLayer messagePersistenceLayer;

    public void sendMessage(
            @NonNull final String mapName,
            @NonNull final String messageType,
            @NonNull final String payload
    ) {
        subject.notifyObserversWith(new Notification<>(MessageBusResponseDto.builder().messages(
                List.of(
                        MessageDto.builder()
                                .uuid(UUID.randomUUID())
                                .payload("test")
                                .build()
                )
        ).build()));

//        messagePersistenceLayer.saveAll(List.of(Message.builder()
//                .mapName(mapName)
//                .messageType(MessageType.valueOf(messageType))
//                .payload(payload)
//                .build()));
    }

}
