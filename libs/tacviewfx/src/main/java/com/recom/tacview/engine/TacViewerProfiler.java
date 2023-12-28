package com.recom.tacview.engine;

import com.recom.tacview.service.profiler.FPSCounter;
import com.recom.tacview.service.profiler.Profiler;
import com.recom.tacview.service.profiler.ProfilerProvider;
import com.recom.tacview.service.profiler.TPSCounter;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class TacViewerProfiler {

    @NonNull
    private final Profiler loopProfiler;

    @NonNull
    private final FPSCounter javaFxCounter;

    @NonNull
    private final FPSCounter fpsCounter;

    @NonNull
    private final TPSCounter tpsCounter;

    public long previousTickNanoTime = 0;
    public long previousFrameNanoTime = 0;


    public TacViewerProfiler(@NonNull final ProfilerProvider profilerProvider) {
        this.loopProfiler = profilerProvider.provide("TacViewerLoop");
        this.javaFxCounter = profilerProvider.provideFPSCounter();
        this.fpsCounter = profilerProvider.provideFPSCounter();
        this.tpsCounter = profilerProvider.provideTPSCounter();
    }

    @NonNull
    public String profileLoop() {
        return String.format("%1s | %2s | %3s | JFX: %4s", tpsCounter.profileTicksPerSecond(), fpsCounter.profileFramesPerSecond(), loopProfiler.stringifyResult(), javaFxCounter.profileFramesPerSecond());
    }

    public void startProfiling() {
        javaFxCounter.startProfiling();
        fpsCounter.startProfiling();
        tpsCounter.resetTicks();
    }

}
