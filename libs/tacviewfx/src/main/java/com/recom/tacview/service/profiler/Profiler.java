package com.recom.tacview.service.profiler;

import com.recom.tacview.engine.units.TimeUnits;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Profiler {

    private final String profilerName;
    private long nanotimeStart;

    @Getter
    private long profiledNanos = 0;
    @Getter
    private double profiledMilliseconds = 0;


    public void startNextMeasurement() {
        nanotimeStart = System.nanoTime();
    }

    public void measureLoop() {
        profiledNanos = System.nanoTime() - nanotimeStart;
        profiledMilliseconds = profiledNanos / TimeUnits.SECOND_IN_NANOS;
    }

    public void printResult() {
        System.out.println(stringifyResult());
    }

    @NonNull
    public String stringifyResult() {
        return String.format("Run %1s in %2s ns |  %s3 ms", profilerName, profiledNanos, profiledMilliseconds);
    }

}
