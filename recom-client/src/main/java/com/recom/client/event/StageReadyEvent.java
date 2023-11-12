package com.recom.client.event;

import javafx.stage.Stage;
import lombok.NonNull;
import org.springframework.context.ApplicationEvent;

public class StageReadyEvent extends ApplicationEvent {

    public StageReadyEvent(@NonNull final Stage stage) {
        super(stage);
    }

    @NonNull
    public Stage getStage() {
        return (Stage) getSource();
    }

}
