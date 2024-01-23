package com.recom.commander.enginemodule.entity.component;

import com.recom.tacview.engine.entitycomponentsystem.component.InputComponent;
import com.recom.tacview.engine.input.command.IsInputCommand;
import com.recom.tacview.engine.input.command.mouse.MouseButtonCommand;
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
            log.info("Input command received: {}", inputCommand);
        }
    }
}
