package com.recom.tacview.engine.input.mapper.mousebutton.fsm;

import com.recom.tacview.engine.input.NanoTimedEvent;
import com.recom.tacview.engine.input.command.mousebutton.IsMouseCommand;
import javafx.scene.input.MouseEvent;
import lombok.NonNull;

import java.util.stream.Stream;

public interface IsMouseButtonEventFSM {

    void iterate();

    void iterate(@NonNull final NanoTimedEvent<MouseEvent> nextNanoTimedMouseEvent);

    void start();

    void stop();

    boolean hasBufferedCommands();

    @NonNull
    Stream<IsMouseCommand<MouseEvent>> popBufferedCommands();


}
