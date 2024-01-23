package com.recom.tacview.engine.input;

import com.recom.tacview.engine.input.command.IsCommand;
import com.recom.tacview.engine.input.mapper.IsInputCommandMapper;
import javafx.scene.input.InputEvent;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.LinkedList;

@Slf4j
@Service
@RequiredArgsConstructor
public class InputManager {

    @Getter
    @NonNull
    private final LinkedList<NanoTimedEvent<? extends InputEvent>> inputEventQueue = new LinkedList<>();

    @NonNull
    private final LinkedList<IsInputCommandMapper<?>> registeredCommandsMappers = new LinkedList<>();

    @NonNull
    private final LinkedList<IsCommand<?>> createdInputCommands = new LinkedList<>();


    public void mapInputEventsToCommands() {
        for (final IsInputCommandMapper<?> mapper : registeredCommandsMappers) {
            if (mapper.mapEvents(inputEventQueue.stream())) {
                createdInputCommands.addAll(mapper.popCreatedCommands());
            }
        }
    }

    public void clearInputQueues() {
        inputEventQueue.clear();
    }

    @NonNull
    public LinkedList<IsCommand<?>> popInputCommands() {
        final LinkedList<IsCommand<?>> createdInputCommandsCopy = new LinkedList<>(createdInputCommands);
        createdInputCommands.clear();

        return createdInputCommandsCopy;
    }

    public void registerCommandMapper(@NonNull final IsInputCommandMapper<?> inputCommandMapper) {
        registeredCommandsMappers.add(inputCommandMapper);
    }

}
