package com.recom.tacview.engine.input.command.mapper;

import com.recom.tacview.engine.input.NanoTimedEvent;
import com.recom.tacview.engine.input.command.IsInputCommand;
import javafx.scene.input.InputEvent;
import lombok.NonNull;

import java.util.LinkedList;
import java.util.stream.Stream;

public interface IsInputCommandMapper {

    boolean mapEvents(@NonNull final Stream<NanoTimedEvent<? extends InputEvent>> inputEventStream);

    @NonNull
    LinkedList<? extends IsInputCommand> popCreatedCommands();

}
