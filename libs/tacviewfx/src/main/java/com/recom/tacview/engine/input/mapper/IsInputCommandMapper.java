package com.recom.tacview.engine.input.mapper;

import com.recom.tacview.engine.input.NanoTimedEvent;
import com.recom.tacview.engine.input.command.IsCommand;
import javafx.scene.input.InputEvent;
import lombok.NonNull;

import java.util.LinkedList;
import java.util.stream.Stream;

public interface IsInputCommandMapper<MAPPED_COMMAND extends IsCommand<? extends InputEvent>> extends AutoCloseable {

    boolean mapEvents(@NonNull final Stream<NanoTimedEvent<? extends InputEvent>> inputEventStream);

    @NonNull
    LinkedList<MAPPED_COMMAND> popCreatedCommands();

}
