package com.recom.tacview.engine.input.command;

import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import lombok.NonNull;

import java.util.stream.Stream;

public abstract class MouseInputCommand implements IsInputCommand {

    public boolean isTriggered(@NonNull final Stream<InputEvent> inputEventSteam) {
        final Stream<MouseEvent> mouseEventStream = inputEventSteam
                .filter(event -> event instanceof MouseEvent)
                .map(event -> (MouseEvent) event);

        return handleMouseEvents(mouseEventStream);
    }

    abstract boolean handleMouseEvents(@NonNull final Stream<MouseEvent> mouseEventStream);

}
