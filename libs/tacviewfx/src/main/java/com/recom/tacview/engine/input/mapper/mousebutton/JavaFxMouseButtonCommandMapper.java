package com.recom.tacview.engine.input.mapper.mousebutton;

import com.recom.tacview.engine.input.NanoTimedEvent;
import com.recom.tacview.engine.input.command.IsCommand;
import com.recom.tacview.engine.input.command.mousebutton.IsMouseCommand;
import com.recom.tacview.engine.input.mapper.IsInputCommandMapper;
import com.recom.tacview.engine.input.mapper.mousebutton.fsm.IsMouseButtonEventFSM;
import com.recom.tacview.engine.input.mapper.mousebutton.fsm.MouseButtonEventFSM1;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class JavaFxMouseButtonCommandMapper implements IsInputCommandMapper<IsMouseCommand<MouseEvent>> {

    @NonNull
    private final LinkedList<NanoTimedEvent<MouseEvent>> unprocessedMouseEvents = new LinkedList<>();
    @NonNull
    private final LinkedList<IsMouseButtonEventFSM> mouseButtonEventFSMs = new LinkedList<>();

    public JavaFxMouseButtonCommandMapper() {
        mouseButtonEventFSMs.add(new MouseButtonEventFSM1(Duration.ofMillis(150), Duration.ofMillis(150), MouseButton.PRIMARY));
        mouseButtonEventFSMs.add(new MouseButtonEventFSM1(Duration.ofMillis(150), Duration.ofMillis(150), MouseButton.SECONDARY));
        mouseButtonEventFSMs.add(new MouseButtonEventFSM1(Duration.ofMillis(150), Duration.ofMillis(150), MouseButton.MIDDLE));

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
        if (unprocessedMouseEvents.isEmpty()) {
            mouseButtonEventFSMs.forEach(IsMouseButtonEventFSM::iterate);
        } else {
            while (!unprocessedMouseEvents.isEmpty()) {
                final NanoTimedEvent<MouseEvent> polledMouseEvent = unprocessedMouseEvents.poll();
                mouseButtonEventFSMs.forEach(fsm -> fsm.iterate(polledMouseEvent));
            }
        }

        return mouseButtonEventFSMs.stream()
                .map(IsMouseButtonEventFSM::hasBufferedCommands)
                .reduce(false, (a, b) -> a || b);
    }

    @NonNull
    @Override
    public LinkedList<IsMouseCommand<MouseEvent>> popCreatedCommands() {
        return mouseButtonEventFSMs.stream()
                .flatMap(IsMouseButtonEventFSM::popBufferedCommands)
                .sorted(Comparator.comparing(IsCommand::getNanos))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public void close() {
        mouseButtonEventFSMs.forEach(IsMouseButtonEventFSM::stop);
        mouseButtonEventFSMs.clear();
        unprocessedMouseEvents.clear();
    }

}
