package com.recom.tacview.engine.entitycomponentsystem.component;

import com.recom.tacview.engine.input.command.IsInputCommand;
import lombok.NonNull;

public abstract class InputComponent extends ComponentTemplate implements HandlesInputCommand {

    public InputComponent() {
        super(ComponentType.InputComponent);
    }

    @Override
    public void handleInputCommand(@NonNull final IsInputCommand inputCommand) {

    }

}
