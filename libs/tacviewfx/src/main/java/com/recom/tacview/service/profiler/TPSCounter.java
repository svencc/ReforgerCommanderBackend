package com.recom.tacview.service.profiler;


import lombok.NoArgsConstructor;

@NoArgsConstructor
public class TPSCounter {

    private long tickCounter = 0;


    public void countTick() {
        tickCounter++;
    }

    public String profileTicksPerSecond() {
        final String profiledPerSecond = String.format("TPS: %1s", tickCounter);
        resetTicks();

        return profiledPerSecond;
    }

    public void resetTicks() {
        tickCounter = 0;
    }

}
