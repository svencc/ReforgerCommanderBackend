package com.recom.service.messagebroker;

import com.recom.entity.Message;
import com.recom.model.message.MessageType;
import com.recom.persistence.message.MessagePersistenceLayer;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageBusService {

    @NonNull
    private final MessagePersistenceLayer messagePersistenceLayer;

    public void sendMessage(@NonNull final String mapName, @NonNull final String messageType, @NonNull final String payload) {
        messagePersistenceLayer.saveAll(List.of(Message.builder()
                .mapName(mapName)
                .messageType(MessageType.valueOf(messageType))
                .payload(payload)
                .build()));
    }

}
