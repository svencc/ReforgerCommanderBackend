package com.recom.persistence.command;

import com.recom.dto.command.CommandDto;
import com.recom.entity.Command;
import com.recom.mapper.CommandMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommandPersistenceLayer {

    @NonNull
    private final CommandRepository commandRepository;
    @NonNull
    private final ConversionService conversionService;

    @NonNull
//    @Cacheable(cacheNames = "CommandPersistenceLayer.findAllMapSpecificCommands") // @TODO there is some work to do, to cache the commands ...
    public List<CommandDto> findAllMapSpecificCommands(@NonNull final String mapName) {
        return commandRepository.findAllByMapName(mapName).stream()
                .map(CommandMapper.INSTANCE::toDto)
                .toList();
    }

    @NonNull
    public List<Command> saveAll(@NonNull final List<Command> entities) {
        return commandRepository.saveAll(entities);
    }

    @NonNull
    public Command save(@NonNull final Command entity) {
        return commandRepository.save(entity);
    }

    public void deleteAll(@NonNull final List<Command> entities) {
        commandRepository.deleteAll(entities);
    }

}
