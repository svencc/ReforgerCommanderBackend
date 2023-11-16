package com.recom.service.messagebus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.recom.dto.message.MessageBusResponseDto;
import com.recom.dto.message.MessageDto;
import com.recom.entity.GameMap;
import com.recom.entity.Message;
import com.recom.model.message.MessageContainer;
import com.recom.observer.HasSubject;
import com.recom.observer.Notification;
import com.recom.observer.Subject;
import com.recom.observer.Subjective;
import com.recom.persistence.message.MessagePersistenceLayer;
import com.recom.service.map.GameMapService;
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
        final List<Message> persistedMessages = persistNotification(messageContainer.getGameMap(), response);
        setLatestMessageTimestamp(response, persistedMessages);
        subject.notifyObserversWith(new Notification<>(response));
    }

    private void setLatestMessageTimestamp(
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

    private @NonNull List<Message> persistNotification(
            @NonNull final GameMap gameMap,
            @NonNull final MessageBusResponseDto messageBusResponse
    ) {
        final List<Message> messagesToSave = messageBusResponse.getMessages().stream()
                .map(message -> Message.builder()
                        .uuid(message.getUuid())
                        .gameMap(gameMap)
                        .messageType(message.getMessageType())
                        .payload(message.getPayload())
                        .timestamp(LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(message.getTimestampEpochMilliseconds())), ZoneId.systemDefault()))
                        .build()
                )
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
                .map(message -> {
                    MessageDto.MessageDtoBuilder messageBuilder = MessageDto.builder()
                            .uuid(UUID.randomUUID())
                            .messageType(message.getMessageType())
                            .timestampEpochMilliseconds(String.valueOf(epochMilli));

                    try {
                        messageBuilder.payload(StaticObjectMapperProvider.provide().writeValueAsString(message.getPayload()));
                    } catch (JsonProcessingException jpe) {
                        log.error("JsonProcessingException: ", jpe);
                    }

                    return messageBuilder.build();
                })
                .toList();

        return MessageBusResponseDto.builder()
                .mapName(container.getGameMap().getName())
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
            @NonNull final GameMap gameMap,
            @Nullable final Long sinceTimestampEpochMilliseconds

    ) {
        final List<MessageDto> messages = messagePersistenceLayer.findAllMapSpecificMessagesSince(gameMap, Optional.ofNullable(sinceTimestampEpochMilliseconds).orElse(0L));
        return MessageBusResponseDto.builder()
                .mapName(gameMap.getName())
                .epochMillisecondsLastMessage(messages.stream()
                        .map(MessageDto::getTimestampEpochMilliseconds)
                        .max(String::compareTo)
                        .orElse(null)
                )
                .messages(messages)
                .build();
    }

}
