package com.recom.tacview.engine.input.command;

import javafx.scene.input.MouseEvent;
import lombok.NonNull;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class MouseClickCommand extends MouseInputCommand {

    private final List<MouseEvent> unprocessedMouseClickStream = new LinkedList<>();

    @Override
    boolean handleMouseEvents(@NonNull final Stream<MouseEvent> mouseEvents) {
        mouseEvents.filter(mouseEvent -> mouseEvent.getEventType() == MouseEvent.MOUSE_CLICKED)
                .forEach(unprocessedMouseClickStream::add);

        return false;
    }


}
