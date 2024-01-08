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
    private final InputCommandQueue inputCommandQueue;

    @NonNull
    private final List<IsInputCommand> registeredInputCommands;


    public void trackInput() {
        for (final IsInputCommand inputCommand : registeredInputCommands) {
            if (inputCommand.isTriggered()) {
                inputCommandQueue.enqueueCommand(inputCommand);
            }
        }
    }

    public void clearInputQueue() {
        inputCommandQueue.clearCommands();
    }

    public void registerInputEvents(@NonNull final IsInputCommand inputEvent) {
        registeredInputCommands.add(inputEvent);
    }

    public void unregisterInputEvent(@NonNull final IsInputCommand inputEvent) {
        registeredInputCommands.remove(inputEvent);
    }

    public void clearRegisteredInputEvents() {
        registeredInputCommands.clear();
    }


}
