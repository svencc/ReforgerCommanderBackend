package com.recom.tacview.engine.input.command.mapper.mouse.fsm;

import com.recom.tacview.engine.input.NanoTimedEvent;
import com.recom.tacview.engine.input.command.mouse.IsMouseCommand;
import javafx.scene.input.MouseEvent;
import lombok.NonNull;

import java.util.LinkedList;
import java.util.stream.Stream;

public interface IsMouseButtonEventFSM {

    void iterate();

    void iterate(@NonNull final NanoTimedEvent<MouseEvent> nextNanoTimedMouseEvent);

    void start();

    void stop();

    boolean hasBufferedCommands();

//    void clearBufferedCommands();

    @NonNull
    Stream<IsMouseCommand> popBufferedCommands();


}
