package com.recom.persistence.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recom.dto.message.MessageDto;
import com.recom.entity.Message;
import com.recom.mapper.MessageMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessagePersistenceLayer {

    @NonNull
    private final MessageRepository messageRepository;
    @NonNull
    private final ConversionService conversionService;
    @NonNull
    private final ObjectMapper objectMapper;

    @NonNull
    public List<MessageDto> findAllMapSpecificMessagesSince(
            @NonNull final String mapName,
            @NonNull final Long sinceTimestampEpochMilliseconds
    ) {
        final LocalDateTime since = LocalDateTime.ofInstant(Instant.ofEpochMilli(sinceTimestampEpochMilliseconds), ZoneId.systemDefault());
        return messageRepository.findAllByMapNameAndTimestampAfter(mapName, since).stream()
                .map(MessageMapper.INSTANCE::toDto)
                .map(messageDto -> {
                    try {
                        final Map<String, Object> map = objectMapper.readValue((String) messageDto.getPayload(), Map.class);
                        messageDto.setPayload(map);
                        return messageDto;
                    } catch (JsonProcessingException e) {
                        log.error("Error while parsing payload of message: {}", messageDto, e);
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(MessageDto::getTimestampEpochMilliseconds))
                .toList();
    }

    @NonNull
    public List<Message> saveAll(@NonNull final List<Message> entities) {
        return messageRepository.saveAll(entities);
    }

    @NonNull
    public Message save(@NonNull final Message entity) {
        return messageRepository.save(entity);
    }

    public void deleteAll(@NonNull final List<Message> entities) {
        messageRepository.deleteAll(entities);
    }

}
