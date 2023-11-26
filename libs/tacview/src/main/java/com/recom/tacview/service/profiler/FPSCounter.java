package com.recom.tacview.service.profiler;


import com.recom.tacview.engine.units.TimeUnits;

public class FPSCounter {

    private static long passedSecondsTrackerMillis = 0;
    private static long frameCounter = 0;

    FPSCounter() {

    }

    public void startProfiling() {
        passedSecondsTrackerMillis = System.currentTimeMillis();
    }

    public void countFrame() {
        frameCounter++;
    }

    public boolean oneSecondPassed() {
        final boolean isOneSecondPassed = System.currentTimeMillis() - passedSecondsTrackerMillis > TimeUnits.SECOND_IN_MILLIS;
        if (isOneSecondPassed) {
            passedSecondsTrackerMillis += TimeUnits.SECOND_IN_MILLIS;
        }

        return isOneSecondPassed;
    }

    public String profileFramesPerSecond() {
        final String profiledPerSecond = "FPS:" + frameCounter;
        resetFrames();

        return profiledPerSecond;
    }

    public void resetFrames() {
        frameCounter = 0;
    }

}
