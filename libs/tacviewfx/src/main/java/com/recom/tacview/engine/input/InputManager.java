package com.recom.tacview.engine.input;

import com.recom.tacview.engine.input.command.IsInputCommand;
import com.recom.tacview.engine.input.command.mapper.IsInputCommandMapper;
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
    private final LinkedList<IsInputCommandMapper> registeredInputCommands = new LinkedList<>();

    @Getter
    @NonNull
    private final LinkedList<IsInputCommand> createdInputCommands = new LinkedList<>();


    public void mapInputEventsToCommands() {
        for (final IsInputCommandMapper mapper : registeredInputCommands) {
            if (mapper.mapEvents(inputEventQueue.stream())) {
                createdInputCommands.addAll(mapper.getCreatedCommands());
            }
        }
    }

    public void clearInputQueues() {
        inputEventQueue.clear();
        createdInputCommands.clear();
    }

    public void registerCommandMapper(@NonNull final IsInputCommandMapper inputCommand) {
        registeredInputCommands.add(inputCommand);
    }

    public void unregisterInputCommand(@NonNull final IsInputCommandMapper inputCommand) {
        registeredInputCommands.remove(inputCommand);
    }

    public void clearRegisteredInputCommands() {
        registeredInputCommands.clear();
    }


}
