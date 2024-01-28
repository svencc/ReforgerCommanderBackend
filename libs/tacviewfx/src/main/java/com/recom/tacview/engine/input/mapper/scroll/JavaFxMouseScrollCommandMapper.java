package com.recom.tacview.engine.input.mapper.scroll;

import com.recom.tacview.engine.input.NanoTimedEvent;
import com.recom.tacview.engine.input.command.scroll.ScrollCommand;
import com.recom.tacview.engine.input.mapper.IsInputCommandMapper;
import javafx.scene.input.InputEvent;
import javafx.scene.input.ScrollEvent;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.stream.Stream;

@Slf4j
public class JavaFxMouseScrollCommandMapper implements IsInputCommandMapper<ScrollCommand> {

    @NonNull
    private final LinkedList<NanoTimedEvent<ScrollEvent>> unprocessedScrollEvents = new LinkedList<>();

    @NonNull
    private final LinkedList<ScrollCommand> mappedCommands = new LinkedList<>();


    @Override
    @SuppressWarnings("unchecked")
    public boolean mapEvents(@NonNull final Stream<NanoTimedEvent<? extends InputEvent>> timedMouseEventStream) {
        timedMouseEventStream
                .filter(event -> event.getEvent() instanceof ScrollEvent)
                .map(event -> (NanoTimedEvent<ScrollEvent>) event)
                .filter(this::isMouseScrollEvent)
                .forEach(unprocessedScrollEvents::add);

        return mapEventsToCommands();
    }

    private boolean mapEventsToCommands() {
        unprocessedScrollEvents.stream()
                .map(ScrollCommand::of)
                .forEach(mappedCommands::add);
        unprocessedScrollEvents.clear();

        return !mappedCommands.isEmpty();
    }

    private boolean isMouseScrollEvent(@NonNull final NanoTimedEvent<ScrollEvent> nanoTimedEvent) {
        return nanoTimedEvent.getEvent().getEventType() == ScrollEvent.SCROLL;
    }

    @NonNull
    @Override
    public LinkedList<ScrollCommand> popCreatedCommands() {
        final LinkedList<ScrollCommand> scrollCommandsCopy = new LinkedList<>(mappedCommands);
        mappedCommands.clear();

        return scrollCommandsCopy;
    }

    @Override
    public void close() {
        unprocessedScrollEvents.clear();
        mappedCommands.clear();
    }

}
