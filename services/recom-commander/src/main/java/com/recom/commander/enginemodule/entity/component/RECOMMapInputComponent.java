package com.recom.commander.enginemodule.entity.component;

import com.recom.tacview.engine.entitycomponentsystem.component.InputComponent;
import com.recom.tacview.engine.input.command.IsInputCommand;
import com.recom.tacview.engine.input.command.MouseClickCommand;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RECOMMapInputComponent extends InputComponent {

    @Override
    public void handleInputCommand(@NonNull IsInputCommand inputCommand) {
        if (inputCommand instanceof MouseClickCommand) {
            final MouseClickCommand mouseClickCommand = (MouseClickCommand) inputCommand;
            log.info("Mouse click command received: {} is doubleClick: {}", mouseClickCommand.getMouseButton(), mouseClickCommand.isDoubleClick());
        }
    }
}
