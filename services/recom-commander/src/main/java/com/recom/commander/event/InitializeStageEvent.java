package com.recom.commander.event;

import javafx.stage.Stage;
import lombok.NonNull;

public class InitializeStageEvent extends LoggableInitializationEvent {

    public InitializeStageEvent(
            @NonNull final Object source,
            @NonNull final Stage stage
    ) {
        super(source, stage);
    }

}
