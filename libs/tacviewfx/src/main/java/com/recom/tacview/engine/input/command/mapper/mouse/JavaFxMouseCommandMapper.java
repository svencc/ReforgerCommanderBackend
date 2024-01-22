package com.recom.tacview.engine.input.command.mapper.mouse;

import com.recom.tacview.engine.input.NanoTimedEvent;
import com.recom.tacview.engine.input.command.IsInputCommand;
import com.recom.tacview.engine.input.command.mapper.IsInputCommandMapper;
import com.recom.tacview.engine.input.command.mapper.mouse.fsm.IsMouseButtonEventFSM;
import com.recom.tacview.engine.input.command.mapper.mouse.fsm.MouseButtonEventFSM1;
import com.recom.tacview.engine.input.command.mouse.IsMouseCommand;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class JavaFxMouseCommandMapper implements IsInputCommandMapper, AutoCloseable {

    @NonNull
    private final LinkedList<NanoTimedEvent<MouseEvent>> unprocessedMouseEvents = new LinkedList<>();
    @NonNull
    private final List<IsMouseButtonEventFSM> mouseButtonEventFSMs;

    public JavaFxMouseCommandMapper() {
        mouseButtonEventFSMs = List.of(
                new MouseButtonEventFSM1(Duration.ofMillis(200), Duration.ofMillis(200), MouseButton.PRIMARY),
                new MouseButtonEventFSM1(Duration.ofMillis(200), Duration.ofMillis(200), MouseButton.SECONDARY),
                new MouseButtonEventFSM1(Duration.ofMillis(200), Duration.ofMillis(200), MouseButton.MIDDLE)
        );

        startMachines();
    }

    private void startMachines() {
        mouseButtonEventFSMs.forEach(IsMouseButtonEventFSM::start);
    }


    @Override
    @SuppressWarnings("unchecked")
    public boolean mapEvents(@NonNull final Stream<NanoTimedEvent<? extends InputEvent>> timedMouseEventStream) {
        timedMouseEventStream
                .filter(event -> event.getEvent() instanceof MouseEvent)
                .map(event -> (NanoTimedEvent<MouseEvent>) event)
                .filter(this::isObservedMouseButtonEvent)
                .forEach(unprocessedMouseEvents::add);

        return runMouseButtonFSM();
    }

    private boolean isObservedMouseButtonEvent(@NonNull final NanoTimedEvent<MouseEvent> nanoTimedEvent) {
        return nanoTimedEvent.getEvent().getEventType() == MouseEvent.MOUSE_PRESSED
                || nanoTimedEvent.getEvent().getEventType() == MouseEvent.MOUSE_RELEASED
                || nanoTimedEvent.getEvent().getEventType() == MouseEvent.MOUSE_DRAGGED;
    }

    private boolean runMouseButtonFSM() {
        while (!unprocessedMouseEvents.isEmpty()) {
            final NanoTimedEvent<MouseEvent> polledMouseEvent = unprocessedMouseEvents.poll();
            mouseButtonEventFSMs.forEach(fsm -> fsm.iterate(polledMouseEvent));
        }
        mouseButtonEventFSMs.forEach(IsMouseButtonEventFSM::iterate);

        return mouseButtonEventFSMs.stream()
                .map(IsMouseButtonEventFSM::hasBufferedCommands)
                .reduce(false, (a, b) -> a || b);
    }

    @NonNull
    @Override
    public LinkedList<IsMouseCommand> popCreatedCommands() {
        final LinkedList<IsMouseCommand> collectedCommands = mouseButtonEventFSMs.stream()
                .flatMap(IsMouseButtonEventFSM::popBufferedCommands)
                .sorted(Comparator.comparing(IsInputCommand::getNanos))
                .collect(Collectors.toCollection(LinkedList::new));


            return collectedCommands;
    }

    @Override
    public void close() {
        mouseButtonEventFSMs.forEach(IsMouseButtonEventFSM::stop);
    }

}
