package com.recom.tacview.engine.input.mapper.keyboard;

import com.recom.tacview.engine.input.NanoTimedEvent;
import com.recom.tacview.engine.input.command.keyboard.KeyboardCommand;
import javafx.event.EventType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.stream.Stream;

import static javafx.scene.input.KeyEvent.KEY_PRESSED;
import static javafx.scene.input.KeyEvent.KEY_RELEASED;

@Slf4j
public class KeyboardCommandGenerator {

    @NonNull
    private final HashMap<KeyCode, NanoTimedEvent<KeyEvent>> pressedKeyEventsBuffer = new HashMap<>();

    @NonNull
    private final LinkedList<KeyboardCommand> bufferedCommands = new LinkedList<>();

    @NonNull
    private final HashSet<KeyCode> ignoredKeyCodes = new HashSet<>();

    public KeyboardCommandGenerator() {
        ignoredKeyCodes.add(KeyCode.WINDOWS);
    }

    public void generate() {
        pressedKeyEventsBuffer.forEach((keyCode, nanoTimedEvent) -> {
            bufferedCommands.add(KeyboardCommand.of(nanoTimedEvent));
        });
    }

    public void updateKeyStates(@NonNull final NanoTimedEvent<KeyEvent> nextKeyEventNanoTimedKeyEvent) {
        final EventType<KeyEvent> eventType = nextKeyEventNanoTimedKeyEvent.getEvent().getEventType();
        final KeyCode keyCode = nextKeyEventNanoTimedKeyEvent.getEvent().getCode();
        if (ignoredKeyCodes.contains(keyCode)) {
            // do nothing
        } else if (eventType == KEY_PRESSED) {
            pressedKeyEventsBuffer.put(nextKeyEventNanoTimedKeyEvent.getEvent().getCode(), nextKeyEventNanoTimedKeyEvent);
        } else if (eventType == KEY_RELEASED) {
            pressedKeyEventsBuffer.remove(nextKeyEventNanoTimedKeyEvent.getEvent().getCode());
        }
    }

    public boolean hasBufferedCommands() {
        return !bufferedCommands.isEmpty();
    }

    @NonNull
    public Stream<KeyboardCommand> popBufferedCommands() {
        final LinkedList<KeyboardCommand> bufferedCommandsCopy = new LinkedList<>(bufferedCommands);
        bufferedCommands.clear();

        return bufferedCommandsCopy.stream();
    }

    public void clear() {
        pressedKeyEventsBuffer.clear();
        bufferedCommands.clear();
    }

}
