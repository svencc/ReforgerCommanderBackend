package com.recom.tacview.engine.input.command.mousebutton;

import com.recom.tacview.engine.input.NanoTimedEvent;
import javafx.scene.input.MouseEvent;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class MouseDragCommand implements IsMouseCommand<MouseEvent> {

    private final boolean dragStart;
    private final boolean dragStop;
    @NonNull
    private final NanoTimedEvent<MouseEvent> nanoTimedEvent;
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
            @NonNull final NanoTimedEvent<MouseEvent> nanoTimedEvent,
            final boolean startMouseDrag,
            final boolean stopMouseDrag
    ) {
        this.nanoTimedEvent = nanoTimedEvent;
        this.dragStart = startMouseDrag;
        this.dragStop = stopMouseDrag;
        this.mouseButton = determineMouseButton(nanoTimedEvent.getEvent());
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
        return nanoTimedEvent.getEvent().getX();
    }

    @Override
    public double getPositionY() {
        return nanoTimedEvent.getEvent().getY();
    }

    @Override
    public long getNanos() {
        return nanoTimedEvent.getNanos();
    }

}
