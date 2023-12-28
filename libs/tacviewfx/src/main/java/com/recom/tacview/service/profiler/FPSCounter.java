package com.recom.tacview.service.profiler;


import com.recom.tacview.engine.units.TimeUnits;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor
public class FPSCounter {

    private long passedSecondsTrackerMillis = 0;
    private long frameCounter = 0;


    public void startProfiling() {
        passedSecondsTrackerMillis = System.currentTimeMillis();
    }

    public void countFrame() {
        frameCounter++;
    }

    public boolean oneSecondPassed() {
        final boolean isOneSecondPassed = System.currentTimeMillis() - passedSecondsTrackerMillis > TimeUnits.SECOND_IN_MILLIS;
        if (isOneSecondPassed) {
            passedSecondsTrackerMillis += (long) TimeUnits.SECOND_IN_MILLIS;
        }

        return isOneSecondPassed;
    }

    @NonNull
    public String profileFramesPerSecond() {
        final String profiledPerSecond = String.format("FPS: %1s", frameCounter);
        resetFrames();

        return profiledPerSecond;
    }

    public void resetFrames() {
        frameCounter = 0;
    }

}
