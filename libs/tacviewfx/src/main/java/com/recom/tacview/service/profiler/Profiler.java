package com.recom.tacview.service.profiler;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Profiler {

    @NonNull
    private final String profilerName;
    private long nanoTimeStart;
    @Getter
    private long profiledNanos = 0;


    public void startNextMeasurement() {
        nanoTimeStart = System.nanoTime();
    }

    public void measureLoop() {
        profiledNanos = System.nanoTime() - nanoTimeStart;
    }

    @NonNull
    public String stringifyResult() {
        return String.format("Run %1s in %2s ns", profilerName, profiledNanos);
    }

}
