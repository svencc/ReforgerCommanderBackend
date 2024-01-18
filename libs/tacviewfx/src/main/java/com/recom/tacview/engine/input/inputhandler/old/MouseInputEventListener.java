package com.recom.tacview.engine.input.inputhandler.old;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MouseInputEventListener implements EventHandler<MouseEvent> {

    @Override
    public void handle(@NonNull final MouseEvent event) {
        log.info("Mouse event: {}", event);
    }

}
