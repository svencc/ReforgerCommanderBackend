package com.recom.tacview.engine.module;

import com.recom.tacview.engine.Updatable;
import com.recom.tacview.engine.entity.Environment;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public abstract class EngineModule implements Updatable {

    @Getter
    @NonNull
    private final Environment environment;


    public void run() {
        init();
        startEngineModule();
    }

    public abstract void init();

    public abstract void startEngineModule();


    public void update(final long elapsedNanoTime) {
        environment.update(elapsedNanoTime);
    }


    public void handleInput() {
        // @TODO: Handle input
    }

}
