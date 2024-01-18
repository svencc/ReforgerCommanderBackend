package com.recom.tacview.engine.input.inputhandler;

import com.recom.tacview.engine.input.InputManager;
import javafx.event.EventHandler;
import javafx.scene.input.InputEvent;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenericInputEventListener implements EventHandler<InputEvent> {

    @NonNull
    private final InputManager inputManager;

    @Override
    public void handle(@NonNull final InputEvent event) {
        inputManager.getInputEventQueue().enqueueEvent(event);
    }

}
