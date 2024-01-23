package com.recom.commander.enginemodule.entity.component;

import com.recom.tacview.engine.entitycomponentsystem.component.InputComponent;
import com.recom.tacview.engine.input.command.IsInputCommand;
import com.recom.tacview.engine.input.command.mouse.MouseButtonCommand;
import javafx.event.Event;
import javafx.scene.input.MouseEvent;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RECOMMapInputComponent extends InputComponent {

    @Override
    public void handleInputCommand(@NonNull IsInputCommand inputCommand) {
        if (inputCommand instanceof MouseButtonCommand) {
            final MouseButtonCommand mouseButtonCommand = (MouseButtonCommand) inputCommand;
            log.info("Mouse click command received: {} is doubleClick: {}", mouseButtonCommand.getMouseButton(), mouseButtonCommand.isDoubleClick());
        } else {
            Event event = inputCommand.getNanoTimedEvent().getEvent();
            if (event instanceof javafx.scene.input.MouseEvent) {
                final MouseEvent mouseEvent = (MouseEvent) event;
                log.info("Input command received: {} ({}) -> {}", inputCommand.getClass().getSimpleName(), mouseEvent.getButton(), inputCommand.getNanoTimedEvent().getEvent().getEventType());
            } else {
                log.info("Input command received: {}  -> {}", inputCommand.getClass().getSimpleName(), inputCommand.getNanoTimedEvent().getEvent().getEventType());
            }

        }
    }
}
