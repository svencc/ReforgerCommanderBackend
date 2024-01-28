package com.recom.tacview.engine.input.command;

import com.recom.tacview.engine.input.NanoTimedEvent;
import javafx.scene.input.InputEvent;
import lombok.NonNull;

public interface IsCommand<T extends InputEvent> {

    long getNanos();

    @NonNull
    NanoTimedEvent<T> getNanoTimedEvent();

}
