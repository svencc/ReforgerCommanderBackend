package com.recom.tacview.engine.input.command.scroll;

import com.recom.tacview.engine.input.NanoTimedEvent;
import com.recom.tacview.engine.input.command.IsCommand;
import javafx.scene.input.ScrollEvent;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class ScrollCommand implements IsCommand<ScrollEvent> {

    @NonNull
    private final NanoTimedEvent<ScrollEvent> nanoTimedEvent;


    @NonNull
    public static ScrollCommand of(@NonNull final NanoTimedEvent<ScrollEvent> nanoTimedEvent) {
        return new ScrollCommand(nanoTimedEvent);
    }

    public ScrollCommand(@NonNull final NanoTimedEvent<ScrollEvent> nanoTimedEvent) {
        this.nanoTimedEvent = nanoTimedEvent;
    }

    public double getPositionX() {
        return nanoTimedEvent.getEvent().getX();
    }

    public double getPositionY() {
        return nanoTimedEvent.getEvent().getY();
    }

    public long getNanos() {
        return nanoTimedEvent.getNanos();
    }

}
