package com.recom.tacview.engine.module;

import com.recom.tacview.engine.entity.environment.EnvironmentBase;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public abstract class EngineModule {

    @Getter
    @NonNull
    private final EnvironmentBase environment;


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
