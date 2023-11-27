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
    private long profiledNanos;
    @Getter
    private double profiledMilliseconds;


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
        return "Run " + profilerName + " in " + profiledNanos + " ns | " + profiledMilliseconds + " ms";
    }

}
