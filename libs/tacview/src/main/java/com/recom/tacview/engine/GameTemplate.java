package com.recom.tacview.engine;

public abstract class GameTemplate {

    public void run() {
        init();
        startGame();
    }

    public abstract void init();

    public abstract void startGame();

}
