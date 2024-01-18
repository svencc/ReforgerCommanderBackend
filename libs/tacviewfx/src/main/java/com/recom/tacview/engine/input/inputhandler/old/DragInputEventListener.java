package com.recom.tacview.engine.input.inputhandler.old;

import javafx.event.EventHandler;
import javafx.scene.input.DragEvent;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DragInputEventListener implements EventHandler<DragEvent> {

    @Override
    public void handle(@NonNull final DragEvent event) {
        log.info("Drag event: {}", event);
    }

}
