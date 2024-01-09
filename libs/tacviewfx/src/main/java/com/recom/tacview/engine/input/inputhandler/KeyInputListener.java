package com.recom.tacview.engine.input.inputhandler;

import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KeyInputListener implements EventHandler<KeyEvent> {

    @Override
    public void handle(@NonNull final KeyEvent event) {
        log.info("Key event: {}", event);
    }

}
