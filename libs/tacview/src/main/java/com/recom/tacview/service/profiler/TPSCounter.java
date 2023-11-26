package com.recom.tacview.service.profiler;


public class TPSCounter {

    private static long tickCounter = 0;

    TPSCounter() {

    }

    public void countTick() {
        tickCounter++;
    }

    public String profileTicksPerSecond() {
        final String profiledPerSecond = "TPS:" + tickCounter;
        resetTicks();

        return profiledPerSecond;
    }

    public void resetTicks() {
        tickCounter = 0;
    }

}
