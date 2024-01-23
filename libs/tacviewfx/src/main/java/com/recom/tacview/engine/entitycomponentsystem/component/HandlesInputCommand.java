package com.recom.tacview.engine.entitycomponentsystem.component;

import com.recom.tacview.engine.input.command.IsCommand;
import lombok.NonNull;

public interface HandlesInputCommand {

    void handleInputCommand(@NonNull final IsCommand inputCommand);

}
