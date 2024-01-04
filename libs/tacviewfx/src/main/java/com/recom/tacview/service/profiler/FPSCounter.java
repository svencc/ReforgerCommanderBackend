package com.recom.tacview.service.profiler;


import lombok.NoArgsConstructor;

@NoArgsConstructor
public class FPSCounter {

    private long frameCounter = 0;


    public void countFrame() {
        frameCounter++;
    }

    public String profileFramesPerSecond() {
        final String profiledPerSecond = String.format("FPS: %1s", frameCounter);
        resetTicks();

        return profiledPerSecond;
    }

    public void resetTicks() {
        frameCounter = 0;
    }

}
