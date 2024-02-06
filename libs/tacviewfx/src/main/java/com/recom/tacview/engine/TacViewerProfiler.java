package com.recom.tacview.engine;

import com.recom.tacview.service.profiler.FPSCounter;
import com.recom.tacview.service.profiler.LoopCounter;
import com.recom.tacview.service.profiler.ProfilerProvider;
import com.recom.tacview.service.profiler.TPSCounter;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class TacViewerProfiler {

    @NonNull
    private final LoopCounter loopCounter;
    @NonNull
    private final FPSCounter fpsCounter;
    @NonNull
    private final TPSCounter tpsCounter;
    public long previousTickNanoTime = 0;
    public long previousFrameNanoTime = 0;
    public long inputHandlingNanoTime = 0;


    public TacViewerProfiler(@NonNull final ProfilerProvider profilerProvider) {
        this.loopCounter = profilerProvider.provideLoopCounter();
        this.fpsCounter = profilerProvider.provideFPSCounter();
        this.tpsCounter = profilerProvider.provideTPSCounter();
    }

    @NonNull
    public String writeProfile() {
        final String loopsPerSecond = loopCounter.profileLoopsPerSecond();
        final String profiledInputNanoTime = String.format("InHandler: %1$,.3f ms", inputHandlingNanoTime / 1_000_000.0);
        final String tps = tpsCounter.profileTicksPerSecond();
        final String fps = fpsCounter.profileFramesPerSecond();

        return String.format("%1s | %2s | %3s | JFX: %4s", profiledInputNanoTime, tps, fps, loopsPerSecond);
    }

    public void startProfiling() {
        loopCounter.startProfiling();
        fpsCounter.resetTicks();
        tpsCounter.resetTicks();
    }

}
