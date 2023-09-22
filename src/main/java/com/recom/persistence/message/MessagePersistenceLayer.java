package com.recom.persistence.message;

import com.recom.dto.message.CommandDto;
import com.recom.entity.Message;
import com.recom.mapper.CommandMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessagePersistenceLayer {

    @NonNull
    private final MessageRepository messageRepository;
    @NonNull
    private final ConversionService conversionService;

    @NonNull
//    @Cacheable(cacheNames = "CommandPersistenceLayer.findAllMapSpecificCommands") // @TODO there is some work to do, to cache the commands ...
    public List<CommandDto> findAllMapSpecificMessages(@NonNull final String mapName) {
        return messageRepository.findAllByMapName(mapName).stream()
                .map(CommandMapper.INSTANCE::toDto)
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
