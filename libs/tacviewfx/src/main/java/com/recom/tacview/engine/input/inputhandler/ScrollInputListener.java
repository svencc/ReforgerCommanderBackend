package com.recom.tacview.engine.input.inputhandler;

import javafx.event.EventHandler;
import javafx.scene.input.ScrollEvent;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ScrollInputListener implements EventHandler<ScrollEvent> {

    @Override
    public void handle(@NonNull final ScrollEvent event) {
        log.info("Scroll event: {}", event);
    }

}
