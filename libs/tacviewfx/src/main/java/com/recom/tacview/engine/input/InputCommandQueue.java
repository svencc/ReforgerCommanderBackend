package com.recom.tacview.engine.input;

import com.recom.tacview.engine.input.command.IsInputCommand;
import com.recom.tacview.engine.input.command.NullCommand;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InputCommandQueue {

    @NonNull
    private final LinkedList<IsInputCommand> commandsQueue = new LinkedList<>();

    @NonNull
    public IsInputCommand dequeueCommand() {
        if (commandsQueue.isEmpty()) {
            return NullCommand.INSTANCE;
        } else {
            return commandsQueue.poll();
        }
    }

    public void enqueueCommand(@NonNull final IsInputCommand inputCommand) {
        commandsQueue.add(inputCommand);
    }

    public void addCommands(@NonNull final List<IsInputCommand> inputCommands) {
        this.commandsQueue.addAll(inputCommands);
    }

    public void clearCommands() {
        commandsQueue.clear();
    }

    public boolean hasCommand() {
        return !commandsQueue.isEmpty();
    }

}
