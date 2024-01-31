package com.recom.tacview.engine.input.command.mousebutton;

import com.recom.tacview.engine.input.NanoTimedEvent;
import javafx.scene.input.MouseEvent;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class MouseButtonCommand implements IsMouseCommand<MouseEvent> {

    private final long timeBetweenDragStartAndDragStop;
    private final boolean probableDraggingIntention;

    @NonNull
    private final NanoTimedEvent<MouseEvent> nanoTimedEvent;
    private final boolean doubleClick;
    @NonNull
    private final MouseButton mouseButton;


    @NonNull
    public static MouseButtonCommand singleClickCommand(
            @NonNull final NanoTimedEvent<MouseEvent> nanoTimedMouseEvent,
            long timeBetweenDragStartAndDragStop,
            long dragToClickThresholdNanos
    ) {

        final boolean isProbableDraggingIntention = timeBetweenDragStartAndDragStop > dragToClickThresholdNanos;
        return new MouseButtonCommand(nanoTimedMouseEvent, false, timeBetweenDragStartAndDragStop, isProbableDraggingIntention);
    }

    @NonNull
    public static MouseButtonCommand doubleClickCommand(@NonNull final NanoTimedEvent<MouseEvent> nanoTimedMouseEvent) {
        return new MouseButtonCommand(nanoTimedMouseEvent, true, 0, false);
    }

    public MouseButtonCommand(
            @NonNull final NanoTimedEvent<MouseEvent> nanoTimedEvent,
            final boolean isDoubleClick,
            final long timeBetweenDragStartAndDragStop,
            final boolean probableDraggingIntention
    ) {
        this.nanoTimedEvent = nanoTimedEvent;
        this.doubleClick = isDoubleClick;
        this.timeBetweenDragStartAndDragStop = timeBetweenDragStartAndDragStop;
        this.mouseButton = determineMouseButton(nanoTimedEvent.getEvent());
        this.probableDraggingIntention = probableDraggingIntention;
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
