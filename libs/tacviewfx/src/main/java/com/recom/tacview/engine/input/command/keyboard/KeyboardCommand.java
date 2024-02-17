package com.recom.tacview.engine.input.command.keyboard;

import com.recom.tacview.engine.input.NanoTimedEvent;
import com.recom.tacview.engine.input.command.IsCommand;
import javafx.scene.input.KeyEvent;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class KeyboardCommand implements IsCommand<KeyEvent> {

    @NonNull
    private final NanoTimedEvent<KeyEvent> nanoTimedEvent;
    private final boolean buttonReleased;

    @NonNull
    public static KeyboardCommand of(@NonNull final NanoTimedEvent<KeyEvent> nanoTimedEvent) {
        return new KeyboardCommand(nanoTimedEvent, false);
    }

    @NonNull
    public static KeyboardCommand released(@NonNull final NanoTimedEvent<KeyEvent> nanoTimedEvent) {
        return new KeyboardCommand(nanoTimedEvent, true);
    }

    public KeyboardCommand(
            @NonNull final NanoTimedEvent<KeyEvent> nanoTimedEvent,
            final boolean buttonReleased
    ) {
        this.nanoTimedEvent = nanoTimedEvent;
        this.buttonReleased = buttonReleased;
    }

    public long getNanos() {
        return nanoTimedEvent.getNanos();
    }

}
