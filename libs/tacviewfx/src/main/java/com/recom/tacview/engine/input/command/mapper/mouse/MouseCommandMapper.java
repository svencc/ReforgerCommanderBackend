package com.recom.tacview.engine.input.command.mapper.mouse;

import com.recom.tacview.engine.input.NanoTimedEvent;
import com.recom.tacview.engine.input.command.MouseClickCommand;
import com.recom.tacview.engine.input.command.mapper.IsInputCommandMapper;
import com.recom.tacview.engine.input.command.mapper.mouse.fsm.MouseEventMachine;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.LinkedList;
import java.util.stream.Stream;

@Slf4j
public class MouseCommandMapper implements IsInputCommandMapper {

    @NonNull
    private final LinkedList<NanoTimedEvent<? extends InputEvent>> unprocessedMouseClicks = new LinkedList<>();
    @NonNull
    private final MouseEventMachine fsm = new MouseEventMachine(Duration.ofMillis(200));


    @Override
    public boolean mapEvents(@NonNull final Stream<NanoTimedEvent<? extends InputEvent>> timedMouseEventStream) {
        timedMouseEventStream
                .filter(nanoTimedEvent -> nanoTimedEvent.getEvent().getEventType() == MouseEvent.MOUSE_CLICKED)
                .forEach(unprocessedMouseClicks::add);

        return runMouseClickFSM();
    }

    @SuppressWarnings("unchecked")
    private boolean runMouseClickFSM() {
        while (!unprocessedMouseClicks.isEmpty()) {
            fsm.iterate((NanoTimedEvent<MouseEvent>) unprocessedMouseClicks.poll());
        }
        fsm.iterate();

        return !fsm.getBufferedCommands().isEmpty();
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public LinkedList<MouseClickCommand> getCreatedCommands() {
        final LinkedList<MouseClickCommand> createdCommands = (LinkedList<MouseClickCommand>) fsm.getBufferedCommands().clone();
        fsm.getBufferedCommands().clear();

        return createdCommands;
    }

}
