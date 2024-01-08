package com.recom.tacview.engine.input.command;

import lombok.NonNull;

public class NullCommand implements IsInputCommand {

    @NonNull
    public static final NullCommand INSTANCE = new NullCommand();

    private NullCommand() {
    }

    @Override
    public boolean isTriggered() {
        return false;
    }

}
