package com.recom.tacview.engine.module;

import com.recom.tacview.engine.entity.World;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public abstract class EngineModuleTemplate {

    @NonNull
    private final World world;


    public void run() {
        init();
        startEngineModule();
    }

    public abstract void init();

    public abstract void startEngineModule();


    public void update() {
        world.update();
    }


    public void handleInput() {
        // @TODO: Handle input
    }

}
