package com.recom.commander.enginemodule.entity.component;

import com.recom.tacview.engine.entitycomponentsystem.component.InputComponent;
import com.recom.tacview.engine.input.command.IsCommand;
import com.recom.tacview.engine.input.command.keyboard.KeyboardCommand;
import com.recom.tacview.engine.input.command.mousebutton.MouseButtonCommand;
import com.recom.tacview.engine.input.command.mousebutton.MouseDragCommand;
import com.recom.tacview.engine.input.command.scroll.ScrollCommand;
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
            log.info("MouseButtonCommand received: {} (doubleClick: {})", mouseButtonCommand.getMouseButton(), mouseButtonCommand.isDoubleClick());
        } else if (inputCommand instanceof MouseDragCommand mouseDragCommand) {
            log.info("MouseDragCommand received: {} ({})", mouseDragCommand.getMouseButton(), inputCommand.getNanoTimedEvent().getEvent().getEventType());
        } else if (inputCommand instanceof ScrollCommand scrollCommand) {
            log.info("ScrollCommand received: {} ({})", inputCommand.getClass().getSimpleName(), mapToScrollDirection(scrollCommand.getNanoTimedEvent().getEvent()));
        } else if (inputCommand instanceof KeyboardCommand keyboardCommand) {
            log.info("KeyboardCommand received: {} ({})", keyboardCommand.getNanoTimedEvent().getEvent().getEventType(), keyboardCommand.getNanoTimedEvent().getEvent().getCode());
        } else {
            log.info("GenericInputCommand received: {} -> {}", inputCommand.getClass().getSimpleName(), inputCommand.getNanoTimedEvent().getEvent().getEventType());
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
