package com.recom.tacview.engine.input.mapper.keyboard;

import com.recom.tacview.engine.input.NanoTimedEvent;
import com.recom.tacview.engine.input.command.IsCommand;
import com.recom.tacview.engine.input.command.keyboard.KeyboardCommand;
import com.recom.tacview.engine.input.mapper.IsInputCommandMapper;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyEvent;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class JavaFxKeyboardCommandMapper implements IsInputCommandMapper<KeyboardCommand> {

    @NonNull
    private final BlockingQueue<NanoTimedEvent<KeyEvent>> unprocessedKeypressEvents = new LinkedBlockingQueue<>();
    @NonNull
    private final KeyboardCommandGenerator keyboardCommandGenerator = new KeyboardCommandGenerator();


    @Override
    @SuppressWarnings("unchecked")
    public boolean mapEvents(@NonNull final Stream<NanoTimedEvent<? extends InputEvent>> timedKeyboardEventStream) {
        timedKeyboardEventStream
                .filter(event -> event.getEvent() instanceof KeyEvent)
                .map(event -> (NanoTimedEvent<KeyEvent>) event)
                .filter(this::isObservedKeyEvent)
                .forEach(nanoTimedEvent -> {
                    try {
                        unprocessedKeypressEvents.put(nanoTimedEvent);
                    } catch (final InterruptedException e) {
                        log.error("Interrupted while adding keypress event to queue", e);
                    }
                });

        return runKeypresGenerator();
    }

    private boolean isObservedKeyEvent(@NonNull final NanoTimedEvent<KeyEvent> nanoTimedEvent) {
        return nanoTimedEvent.getEvent().getEventType() == KeyEvent.KEY_PRESSED
                || nanoTimedEvent.getEvent().getEventType() == KeyEvent.KEY_RELEASED;
    }

    private boolean runKeypresGenerator() {
        while (!unprocessedKeypressEvents.isEmpty()) {
            keyboardCommandGenerator.updateKeyStates(unprocessedKeypressEvents.poll());
        }
        keyboardCommandGenerator.generate();

        return keyboardCommandGenerator.hasBufferedCommands();
    }

    @NonNull
    @Override
    public LinkedList<KeyboardCommand> popCreatedCommands() {
        return keyboardCommandGenerator.popBufferedCommands()
                .sorted(Comparator.comparing(IsCommand::getNanos))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public void close() {
        keyboardCommandGenerator.clear();
        unprocessedKeypressEvents.clear();
    }

}
