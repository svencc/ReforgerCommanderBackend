package com.recom.service.messagebus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.recom.dto.message.MessageBusResponseDto;
import com.recom.dto.message.MessageDto;
import com.recom.entity.Message;
import com.recom.model.message.MessageContainer;
import com.recom.observer.HasSubject;
import com.recom.observer.Notification;
import com.recom.observer.Subject;
import com.recom.observer.Subjective;
import com.recom.persistence.message.MessagePersistenceLayer;
import com.recom.service.provider.StaticObjectMapperProvider;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageBusService implements HasSubject<MessageBusResponseDto> {

    @NonNull
    @Getter()
    private final Subjective<MessageBusResponseDto> subject = new Subject<>();
    @NonNull
    private final MessagePersistenceLayer messagePersistenceLayer;

    public <T> void sendMessage(@NonNull final MessageContainer messageContainer) {
        final MessageBusResponseDto response = prepareNotification(messageContainer);
        final List<Message> persistedMessages = persistNotification(response);
        setLatestMessage(response, persistedMessages);
        subject.notifyObserversWith(new Notification<>(response));
    }

    private void setLatestMessage(
            @NonNull final MessageBusResponseDto response,
            @NonNull final List<Message> persistedMessages
    ) {
        response.setEpochMillisecondsLastMessage(persistedMessages.stream()
                .mapToLong(message -> message.getTimestamp().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                .boxed()
                .max(Long::compareTo)
                .map(String::valueOf)
                .orElse(null)
        );
    }

    private @NonNull List<Message> persistNotification(@NonNull final MessageBusResponseDto messageBusResponse) {
        final List<Message> messagesToSave = messageBusResponse.getMessages().stream()
                .map(message -> {
                            try {
                                return Message.builder()
                                        .uuid(message.getUuid())
                                        .mapName(messageBusResponse.getMapName())
                                        .messageType(message.getMessageType())
                                        .payload(StaticObjectMapperProvider.provide().writeValueAsString(message.getPayload()))
                                        .timestamp(LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(message.getTimestampEpochMilliseconds())), ZoneId.systemDefault()))
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
            return messagePersistenceLayer.saveAll(messagesToSave);
        } else {
            return List.of();
        }
    }

    @NonNull
    private MessageBusResponseDto prepareNotification(@NonNull final MessageContainer container) {
        final Instant now = Instant.now();
        final long epochMilli = now.toEpochMilli();
        final List<MessageDto> messages = container.getMessages().stream()
                .map(message -> MessageDto.builder()
                        .uuid(UUID.randomUUID())
                        .messageType(message.getMessageType())
                        .payload(message.getPayload())
                        .timestampEpochMilliseconds(String.valueOf(epochMilli))
                        .build())
                .toList();

        return MessageBusResponseDto.builder()
                .mapName(container.getMapName())
                .epochMillisecondsLastMessage(messages.stream()
                        .map(MessageDto::getTimestampEpochMilliseconds)
                        .max(String::compareTo)
                        .orElse(null)
                )
                .messages(messages)
                .build();
    }

    @NonNull
    public MessageBusResponseDto listMessagesSince(
            @NonNull final String mapName,
            @Nullable final Long sinceTimestampEpochMilliseconds

    ) {
        final List<MessageDto> messages = messagePersistenceLayer.findAllMapSpecificMessagesSince(mapName, Optional.ofNullable(sinceTimestampEpochMilliseconds).orElse(0L));
        return MessageBusResponseDto.builder()
                .mapName(mapName)
                .epochMillisecondsLastMessage(messages.stream()
                        .map(MessageDto::getTimestampEpochMilliseconds)
                        .max(String::compareTo)
                        .orElse(null)
                )
                .messages(messages)
                .build();
    }

}
