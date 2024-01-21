package com.recom.tacview.engine.input.command.mapper.mouse.fsm;

import com.recom.tacview.engine.input.NanoTimedEvent;
import com.recom.tacview.engine.input.command.mouse.IsMouseCommand;
import com.recom.tacview.engine.input.command.mouse.MouseButtonCommand;
import javafx.scene.input.MouseEvent;
import lombok.NonNull;

import java.util.LinkedList;

public interface IsMouseEventFSM {

    void iterate();

    void iterate(@NonNull final NanoTimedEvent<MouseEvent> nextNanoTimedMouseEvent);

    @NonNull
    LinkedList<IsMouseCommand> getBufferedCommands();

    void start();

    void stop();


}
