package com.recom.tacview.engine.entitycomponentsystem.component;

import com.recom.tacview.engine.input.command.IsInputCommand;
import lombok.NonNull;

public interface HandlesInput {

    void handleInput(@NonNull final IsInputCommand inputCommand);

}
