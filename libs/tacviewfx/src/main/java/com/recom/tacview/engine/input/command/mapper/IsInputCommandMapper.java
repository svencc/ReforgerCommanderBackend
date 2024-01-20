package com.recom.tacview.engine.input.command.mapper;

import com.recom.tacview.engine.input.NanoTimedEvent;
import com.recom.tacview.engine.input.command.IsInputCommand;
import javafx.scene.input.InputEvent;

import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.Stream;

public interface IsInputCommandMapper {

    boolean mapEvents(final Stream<NanoTimedEvent<? extends InputEvent>> inputEventStream);

    LinkedList<? extends IsInputCommand> getCreatedCommands();

}
