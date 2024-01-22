package com.recom.tacview.engine.input.command.mapper.mouse;

import com.recom.tacview.engine.input.NanoTimedEvent;
import com.recom.tacview.engine.input.command.mapper.IsInputCommandMapper;
import com.recom.tacview.engine.input.command.mapper.mouse.fsm.IsMouseButtonEventFSM;
import com.recom.tacview.engine.input.command.mapper.mouse.fsm.MouseButtonEventFSM1;
import com.recom.tacview.engine.input.command.mouse.MouseButtonCommand;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.LinkedList;
import java.util.stream.Stream;

@Slf4j
public class JavaFxMouseCommandMapper implements IsInputCommandMapper, AutoCloseable {

    @NonNull
    private final LinkedList<NanoTimedEvent<MouseEvent>> unprocessedMouseEvents = new LinkedList<>();
    @NonNull
    private final IsMouseButtonEventFSM primaryMouseButtonFSM = new MouseButtonEventFSM1(Duration.ofMillis(200), Duration.ofMillis(200), MouseButton.PRIMARY);
    @NonNull
    private final IsMouseButtonEventFSM secondaryMouseButtonFSM = new MouseButtonEventFSM1(Duration.ofMillis(200), Duration.ofMillis(200), MouseButton.SECONDARY);

    public JavaFxMouseCommandMapper() {
        primaryMouseButtonFSM.start();
        secondaryMouseButtonFSM.start();
    }


    @Override
    @SuppressWarnings("unchecked")
    public boolean mapEvents(@NonNull final Stream<NanoTimedEvent<? extends InputEvent>> timedMouseEventStream) {
        timedMouseEventStream
                .filter(event -> event.getEvent() instanceof MouseEvent)
                .map(event -> (NanoTimedEvent<MouseEvent>) event)
                .filter(this::isObservedMouseEvent)
                .forEach(unprocessedMouseEvents::add);

        return runMouseClickFSM();
    }

    private boolean isObservedMouseEvent(
            @NonNull final NanoTimedEvent<MouseEvent> nanoTimedEvent
    ) {
        return nanoTimedEvent.getEvent().getEventType() == MouseEvent.MOUSE_PRESSED
                || nanoTimedEvent.getEvent().getEventType() == MouseEvent.MOUSE_RELEASED
                || nanoTimedEvent.getEvent().getEventType() == MouseEvent.MOUSE_DRAGGED;
    }

    private boolean runMouseClickFSM() {
        while (!unprocessedMouseEvents.isEmpty()) {
            final NanoTimedEvent<MouseEvent> polledMouseEvent = unprocessedMouseEvents.poll();
            primaryMouseButtonFSM.iterate(polledMouseEvent);
            secondaryMouseButtonFSM.iterate(polledMouseEvent);
        }
        primaryMouseButtonFSM.iterate();
        secondaryMouseButtonFSM.iterate();

        return !primaryMouseButtonFSM.getBufferedCommands().isEmpty() || !secondaryMouseButtonFSM.getBufferedCommands().isEmpty();
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public LinkedList<MouseButtonCommand> getCreatedCommands() {
        final LinkedList<MouseButtonCommand> createdCommands = (LinkedList<MouseButtonCommand>) primaryMouseButtonFSM.getBufferedCommands().clone();
        createdCommands.addAll((LinkedList<MouseButtonCommand>) secondaryMouseButtonFSM.getBufferedCommands().clone());
        primaryMouseButtonFSM.getBufferedCommands().clear();
        secondaryMouseButtonFSM.getBufferedCommands().clear();

        return createdCommands;
    }

    @Override
    public void close() {
        primaryMouseButtonFSM.stop();
        secondaryMouseButtonFSM.stop();
    }

}
