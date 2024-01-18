package com.recom.tacview.engine.input;

import com.recom.tacview.engine.input.command.IsInputCommand;
import javafx.scene.input.InputEvent;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InputManager {

    @Getter
    @NonNull
    private final List<InputEvent> inputEventQueue = new LinkedList<>();

    @NonNull
    private final List<IsInputCommand> registeredInputCommands = new LinkedList<>();

    @Getter
    @NonNull
    private final List<IsInputCommand> triggeredInputCommands = new LinkedList<>();


    public void mapInputEventsToCommands() {
        for (final IsInputCommand inputCommand : registeredInputCommands) {
            if (inputCommand.isTriggered(inputEventQueue.stream())) {
                triggeredInputCommands.add(inputCommand);
            }
        }
    }

    public void clearInputCommandQueue() {
        inputEventQueue.clear();
    }

    public void registerInputCommand(@NonNull final IsInputCommand inputCommand) {
        registeredInputCommands.add(inputCommand);
    }

    public void unregisterInputCommand(@NonNull final IsInputCommand inputCommand) {
        registeredInputCommands.remove(inputCommand);
    }

    public void clearRegisteredInputCommands() {
        registeredInputCommands.clear();
    }


}
