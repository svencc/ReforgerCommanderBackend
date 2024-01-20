package com.recom.tacview.engine.input.command;

import com.recom.tacview.engine.input.NanoTimedEvent;
import javafx.scene.input.MouseEvent;
import lombok.Getter;
import lombok.NonNull;

public class MouseClickCommand implements IsInputCommand {

    @NonNull
    private final NanoTimedEvent<MouseEvent> nanoTimedMouseEvent;
    @Getter
    private final boolean isDoubleClick;
    @Getter
    @NonNull
    private final MouseButton mouseButton;


    @NonNull
    public static MouseClickCommand singleClickCommand(
            @NonNull final NanoTimedEvent<MouseEvent> nanoTimedMouseEvent
    ) {
        return new MouseClickCommand(nanoTimedMouseEvent, false);
    }

    @NonNull
    public static MouseClickCommand doubleClickCommand(
            @NonNull final NanoTimedEvent<MouseEvent> nanoTimedMouseEvent
    ) {
        return new MouseClickCommand(nanoTimedMouseEvent, true);
    }

    public MouseClickCommand(
            @NonNull final NanoTimedEvent<MouseEvent> nanoTimedMouseEvent,
            final boolean isDoubleClick
    ) {
        this.nanoTimedMouseEvent = nanoTimedMouseEvent;
        this.isDoubleClick = isDoubleClick;
        this.mouseButton = determineMouseButton(nanoTimedMouseEvent.getEvent());
    }

    @NonNull
    private MouseButton determineMouseButton(@NonNull final MouseEvent event) {
        return switch (event.getButton()) {
            case PRIMARY -> MouseButton.PRIMARY;
            case SECONDARY -> MouseButton.SECONDARY;
            case MIDDLE -> MouseButton.TERNARY;
            default -> MouseButton.OTHER;
        };
    }

    public double getPositionX() {
        return nanoTimedMouseEvent.getEvent().getX();
    }

    public double getPositionY() {
        return nanoTimedMouseEvent.getEvent().getY();
    }

    @NonNull
    public NanoTimedEvent<MouseEvent> getNanoTimedMouseEvent() {
        nanoTimedMouseEvent.getEvent().getEventType();
        nanoTimedMouseEvent.getEvent().getButton();


        return nanoTimedMouseEvent;
    }

    enum MouseButton {
        PRIMARY,
        SECONDARY,
        TERNARY,
        OTHER
    }

}
