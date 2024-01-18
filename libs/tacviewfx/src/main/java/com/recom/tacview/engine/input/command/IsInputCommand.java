package com.recom.tacview.engine.input.command;

import javafx.scene.input.InputEvent;
import lombok.NonNull;

import java.util.List;
import java.util.stream.Stream;

public interface IsInputCommand {

    boolean isTriggered(@NonNull final Stream<InputEvent> inputEventStream);

}
