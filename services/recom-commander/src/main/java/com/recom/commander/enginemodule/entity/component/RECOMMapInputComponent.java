package com.recom.commander.enginemodule.entity.component;

import com.recom.tacview.engine.entitycomponentsystem.component.InputComponent;
import com.recom.tacview.engine.input.command.IsCommand;
import com.recom.tacview.engine.input.command.mousebutton.MouseButtonCommand;
import javafx.event.Event;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RECOMMapInputComponent extends InputComponent {

    @Override
    public void handleInputCommand(@NonNull IsCommand<?> inputCommand) {
        if (inputCommand instanceof MouseButtonCommand mouseButtonCommand) {
            log.info("MouseClickCommand received: {} (doubleClick: {})", mouseButtonCommand.getMouseButton(), mouseButtonCommand.isDoubleClick());
        } else {
            Event event = inputCommand.getNanoTimedEvent().getEvent();
            if (event instanceof MouseEvent mouseEvent) {
                log.info("MouseCommand received: {} ({}) -> {}", inputCommand.getClass().getSimpleName(), mouseEvent.getButton(), inputCommand.getNanoTimedEvent().getEvent().getEventType());
            } else if (event instanceof ScrollEvent scrollEvent) {
                log.info("ScrollCommand received: {} ({})", inputCommand.getClass().getSimpleName(), mapToScrollDirection(scrollEvent));
            } else {
                log.info("GenericInputCommand received: {} -> {}", inputCommand.getClass().getSimpleName(), inputCommand.getNanoTimedEvent().getEvent().getEventType());
            }
        }
    }

    @NonNull
    private String mapToScrollDirection(@NonNull final ScrollEvent scrollEvent) {
        if (scrollEvent.getDeltaX() < 0) {
            return "RIGHT";
        } else if (scrollEvent.getDeltaX() > 0) {
            return "LEFT";
        } else if (scrollEvent.getDeltaY() > 0) {
            return "UP";
        } else if (scrollEvent.getDeltaY() < 0) {
            return "DOWN";
        } else {
            return "NONE";
        }
    }

}
