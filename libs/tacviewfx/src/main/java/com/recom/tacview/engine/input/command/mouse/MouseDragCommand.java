package com.recom.tacview.engine.input.command.mouse;

import com.recom.tacview.engine.input.NanoTimedEvent;
import javafx.scene.input.MouseEvent;
import lombok.Getter;
import lombok.NonNull;

public class MouseDragCommand implements IsMouseCommand<MouseEvent> {

    @Getter
    @NonNull
    private final NanoTimedEvent<MouseEvent> nanoTimedMouseEvent;
    @Getter
    private final boolean dragStart;
    private final boolean dragStop;
    @Getter
    @NonNull
    private final MouseButton mouseButton;

    // send event with current x,y (/)
    // radiant 0
    // distance from source to current position 0
    // vector from source to current position 0 0


    @NonNull
    public static MouseDragCommand dragStartCommand(@NonNull final NanoTimedEvent<MouseEvent> nanoTimedMouseEvent) {
        return new MouseDragCommand(nanoTimedMouseEvent, true, false);
    }

    @NonNull
    public static MouseDragCommand dragStopCommand(@NonNull final NanoTimedEvent<MouseEvent> nanoTimedMouseEvent) {
        return new MouseDragCommand(nanoTimedMouseEvent, false, true);
    }

    @NonNull
    public static MouseDragCommand dragginCommand(@NonNull final NanoTimedEvent<MouseEvent> nanoTimedMouseEvent) {
        return new MouseDragCommand(nanoTimedMouseEvent, false, false);
    }

    public MouseDragCommand(
            @NonNull final NanoTimedEvent<MouseEvent> nanoTimedMouseEvent,
            final boolean startMouseDrag,
            final boolean stopMouseDrag
    ) {
        this.nanoTimedMouseEvent = nanoTimedMouseEvent;
        this.dragStart = startMouseDrag;
        this.dragStop = stopMouseDrag;
        this.mouseButton = determineMouseButton(nanoTimedMouseEvent.getEvent());
    }

    @NonNull
    private MouseButton determineMouseButton(@NonNull final MouseEvent event) {
        return switch (event.getButton()) {
            case PRIMARY -> MouseButton.PRIMARY;
            case SECONDARY -> MouseButton.SECONDARY;
            case MIDDLE -> MouseButton.MIDDLE;
            default -> MouseButton.OTHER;
        };
    }

    public boolean isDragging() {
        return !dragStart && !dragStop;
    }

    @Override
    public double getPositionX() {
        return nanoTimedMouseEvent.getEvent().getX();
    }

    @Override
    public double getPositionY() {
        return nanoTimedMouseEvent.getEvent().getY();
    }

    @NonNull
    public NanoTimedEvent<MouseEvent> getNanoTimedEvent() {
        nanoTimedMouseEvent.getEvent().getEventType();
        nanoTimedMouseEvent.getEvent().getButton();


        return nanoTimedMouseEvent;
    }

    @Override
    public long getNanos() {
        return nanoTimedMouseEvent.getNanos();
    }

}
