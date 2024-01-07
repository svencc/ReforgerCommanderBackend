package com.recom.tacview.service.profiler;

import lombok.NonNull;
import org.springframework.stereotype.Service;

@Service
public final class ProfilerProvider {

    @NonNull
    public Profiler provide(@NonNull final String profilerName) {
        return new Profiler(profilerName);
    }

    @NonNull
    public LoopCounter provideLoopCounter() {
        return new LoopCounter();
    }

    @NonNull
    public FPSCounter provideFPSCounter() {
        return new FPSCounter();
    }

    @NonNull
    public TPSCounter provideTPSCounter() {
        return new TPSCounter();
    }

}
