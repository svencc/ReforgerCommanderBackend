package com.recom.tacview.engine.input.command.mouse;

import com.recom.tacview.engine.input.NanoTimedEvent;
import javafx.scene.input.MouseEvent;
import lombok.Getter;
import lombok.NonNull;

public class MouseButtonCommand implements IsMouseCommand<MouseEvent> {

    @NonNull
    private final NanoTimedEvent<MouseEvent> nanoTimedMouseEvent;
    @Getter
    private final boolean doubleClick;
    @Getter
    @NonNull
    private final MouseButton mouseButton;


    @NonNull
    public static MouseButtonCommand singleClickCommand(
            @NonNull final NanoTimedEvent<MouseEvent> nanoTimedMouseEvent
    ) {
        return new MouseButtonCommand(nanoTimedMouseEvent, false);
    }

    @NonNull
    public static MouseButtonCommand doubleClickCommand(@NonNull final NanoTimedEvent<MouseEvent> nanoTimedMouseEvent) {
        return new MouseButtonCommand(nanoTimedMouseEvent, true);
    }

    public MouseButtonCommand(
            @NonNull final NanoTimedEvent<MouseEvent> nanoTimedMouseEvent,
            final boolean isDoubleClick
    ) {
        this.nanoTimedMouseEvent = nanoTimedMouseEvent;
        this.doubleClick = isDoubleClick;
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
