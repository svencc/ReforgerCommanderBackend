package com.recom.tacview.service.profiler;

import com.recom.tacview.engine.units.TimeUnits;
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
    @Getter
    private double profiledMilliseconds = 0;


    public void startNextMeasurement() {
        nanoTimeStart = System.nanoTime();
    }

    public void measureLoop() {
        profiledNanos = System.nanoTime() - nanoTimeStart;
        profiledMilliseconds = profiledNanos / TimeUnits.SECOND_IN_NANOS;
    }

    @NonNull
    public String stringifyResult() {
        return String.format("Run %1s in %2s ns / %s3 ms", profilerName, profiledNanos, profiledMilliseconds);
    }

}
