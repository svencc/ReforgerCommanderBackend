package com.recom.tacview.engine.input;

import com.recom.tacview.engine.input.command.IsInputCommand;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InputManager {

    @Getter
    @NonNull
    private final InputEventQueue inputEventQueue;

    @NonNull
    private final List<IsInputCommand> registeredInputCommands;

    @Getter
    @NonNull
    private final List<IsInputCommand> triggeredInputCommands;


    public void mapInputEventsToCommands() {
        for (final IsInputCommand inputCommand : registeredInputCommands) {
            if (inputCommand.isTriggered()) {
                triggeredInputCommands.add(inputCommand);
            }
        }
        inputEventQueue.clearEvents();
    }

    public void clearInputCommandQueue() {
        inputEventQueue.clearEvents();
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
