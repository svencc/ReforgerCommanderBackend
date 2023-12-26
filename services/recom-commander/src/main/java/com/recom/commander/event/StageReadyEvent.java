package com.recom.commander.event;

import javafx.stage.Stage;
import lombok.NonNull;

public class StageReadyEvent extends LoggableInitializationEvent {

    public StageReadyEvent(
            @NonNull final Object source,
            @NonNull final Stage stage
    ) {
        super(source, stage);
    }

}
