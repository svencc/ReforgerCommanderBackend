package com.recom.tacview.service.profiler;


import com.recom.tacview.engine.units.TimeUnits;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor
public class LoopCounter {

    private long lastProfileIterationInMillis = 0;
    private long loopCounter = 0;


    public void startProfiling() {
        lastProfileIterationInMillis = System.currentTimeMillis();
    }

    public void countLoop() {
        loopCounter++;
    }

    public boolean isOneSecondPassed() {
        final long now = System.currentTimeMillis();
        final boolean isOneSecondPassed = now - lastProfileIterationInMillis > TimeUnits.SECOND_IN_MILLIS;
        if (isOneSecondPassed) {
            lastProfileIterationInMillis = now;
        }

        return isOneSecondPassed;
    }

    @NonNull
    public String profileLoopsPerSecond() {
        final String profiledLoopsPerSecond = String.format("LPS: %1s", loopCounter);
        resetFrames();

        return profiledLoopsPerSecond;
    }

    public void resetFrames() {
        loopCounter = 0;
    }

}
