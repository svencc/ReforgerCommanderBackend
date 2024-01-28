package com.recom.tacview.engine.input.command.mousebutton;

import com.recom.tacview.engine.input.NanoTimedEvent;
import javafx.scene.input.MouseEvent;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class MouseButtonCommand implements IsMouseCommand<MouseEvent> {

    @NonNull
    private final NanoTimedEvent<MouseEvent> nanoTimedEvent;
    private final boolean doubleClick;
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
            @NonNull final NanoTimedEvent<MouseEvent> nanoTimedEvent,
            final boolean isDoubleClick
    ) {
        this.nanoTimedEvent = nanoTimedEvent;
        this.doubleClick = isDoubleClick;
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
