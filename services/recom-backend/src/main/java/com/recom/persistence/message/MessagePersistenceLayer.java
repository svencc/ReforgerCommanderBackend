package com.recom.persistence.message;

import com.recom.dto.message.MessageDto;
import com.recom.entity.GameMap;
import com.recom.entity.Message;
import com.recom.mapper.MessageMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessagePersistenceLayer {

    @NonNull
    private final MessageRepository messageRepository;


    @NonNull
    public List<MessageDto> findAllMapSpecificMessagesSince(
            @NonNull final GameMap gameMap,
            @NonNull final Long sinceTimestampEpochMilliseconds
    ) {
        final LocalDateTime since = LocalDateTime.ofInstant(Instant.ofEpochMilli(sinceTimestampEpochMilliseconds), ZoneId.systemDefault());
        return messageRepository.findAllByGameMapAndTimestampAfter(gameMap, since).stream()
                .map(MessageMapper.INSTANCE::toDto)
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
