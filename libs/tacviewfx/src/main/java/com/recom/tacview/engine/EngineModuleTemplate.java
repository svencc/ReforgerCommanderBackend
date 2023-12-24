package com.recom.tacview.engine;

public abstract class EngineModuleTemplate {

    public void run() {
        init();
        startEngineModule();
    }

    public abstract void init();

    public abstract void startEngineModule();

}
