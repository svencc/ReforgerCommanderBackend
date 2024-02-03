package com.recom.tacview.engine.input.mapper.scroll;

import com.recom.tacview.engine.input.NanoTimedEvent;
import com.recom.tacview.engine.input.command.scroll.ScrollCommand;
import com.recom.tacview.engine.input.mapper.IsInputCommandMapper;
import javafx.scene.input.InputEvent;
import javafx.scene.input.ScrollEvent;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Stream;

@Slf4j
public class JavaFxMouseScrollCommandMapper implements IsInputCommandMapper<ScrollCommand> {

    @NonNull
    private final BlockingQueue<NanoTimedEvent<ScrollEvent>> unprocessedScrollEvents = new LinkedBlockingQueue<>();

    @NonNull
    private final LinkedList<ScrollCommand> mappedCommands = new LinkedList<>();


    @Override
    @SuppressWarnings("unchecked")
    public boolean mapEvents(@NonNull final Stream<NanoTimedEvent<? extends InputEvent>> timedMouseScrollEventStream) {
        timedMouseScrollEventStream
                .filter(event -> event.getEvent() instanceof ScrollEvent)
                .map(event -> (NanoTimedEvent<ScrollEvent>) event)
                .filter(this::isMouseScrollEvent)
                .forEach(nanoTimedEvent -> {
                    try {
                        unprocessedScrollEvents.put(nanoTimedEvent);
                    } catch (final InterruptedException e) {
                        log.error("Interrupted while adding keypress event to queue", e);
                    }
                });

        return mapEventsToCommands();
    }

    private boolean mapEventsToCommands() {
        while (!unprocessedScrollEvents.isEmpty()) {
            mappedCommands.add(ScrollCommand.of(unprocessedScrollEvents.poll()));
        }

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
