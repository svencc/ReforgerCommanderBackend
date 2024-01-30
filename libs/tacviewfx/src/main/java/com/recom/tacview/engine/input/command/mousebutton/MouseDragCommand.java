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
    private final NanoTimedEvent<MouseEvent> sourceNanoTimedEvent;
    @NonNull
    private final MouseButton mouseButton;


    public boolean isInOriginPosition() {
        if (sourceNanoTimedEvent.equals(nanoTimedEvent)) {
            return true;
        } else {
            return sourceNanoTimedEvent.getEvent().getX() == nanoTimedEvent.getEvent().getX() &&
                    sourceNanoTimedEvent.getEvent().getY() == nanoTimedEvent.getEvent().getY();
        }
    }

    public double getDistance() {
        return MouseEventCalculator.calculateDistanceBetweenMouseEvents(sourceNanoTimedEvent.getEvent(), nanoTimedEvent.getEvent());
    }

    public double getDistanceX() {
        return nanoTimedEvent.getEvent().getX() - sourceNanoTimedEvent.getEvent().getX();
    }

    public double getDistanceY() {
        return nanoTimedEvent.getEvent().getY() - sourceNanoTimedEvent.getEvent().getY();
    }

    public double getRadiant() {
        return MouseEventCalculator.calculateRadiantBetweenMouseEvents(sourceNanoTimedEvent.getEvent(), nanoTimedEvent.getEvent());
    }

    @NonNull
    public static MouseDragCommand dragStartCommand(@NonNull final NanoTimedEvent<MouseEvent> nanoTimedMouseEvent) {
        return new MouseDragCommand(nanoTimedMouseEvent, nanoTimedMouseEvent, true, false);
    }

    @NonNull
    public static MouseDragCommand dragStopCommand(
            @NonNull final NanoTimedEvent<MouseEvent> sourceNanoTimedMouseEvent,
            @NonNull final NanoTimedEvent<MouseEvent> nanoTimedMouseEvent
    ) {
        return new MouseDragCommand(sourceNanoTimedMouseEvent, nanoTimedMouseEvent, false, true);
    }

    @NonNull
    public static MouseDragCommand dragginCommand(
            @NonNull final NanoTimedEvent<MouseEvent> sourceNanoTimedMouseEvent,
            @NonNull final NanoTimedEvent<MouseEvent> nanoTimedMouseEvent
    ) {
        return new MouseDragCommand(sourceNanoTimedMouseEvent, nanoTimedMouseEvent, false, false);
    }

    public MouseDragCommand(
            @NonNull final NanoTimedEvent<MouseEvent> sourceNanoTimedEvent,
            @NonNull final NanoTimedEvent<MouseEvent> nanoTimedEvent,
            final boolean startMouseDrag,
            final boolean stopMouseDrag
    ) {
        this.sourceNanoTimedEvent = sourceNanoTimedEvent;
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
