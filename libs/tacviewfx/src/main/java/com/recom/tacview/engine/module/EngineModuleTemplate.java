package com.recom.tacview.engine.module;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public abstract class EngineModuleTemplate {


    public void run() {
        init();
        startEngineModule();
    }

    public abstract void init();

    public abstract void startEngineModule();


    public abstract void tick();

    public abstract void handleInput();

}
