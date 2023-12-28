package com.recom.tacview.engine;

import com.recom.tacview.engine.graphics.ScreenComposer;
import com.recom.tacview.engine.module.EngineModuleTemplate;
import com.recom.tacview.property.RendererProperties;
import com.recom.tacview.property.TickProperties;
import com.recom.tacview.service.profiler.ProfilerProvider;
import com.recom.tacview.strategy.ProfileFPSStrategy;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;

@Slf4j
public class TacViewer extends Canvas {

    @NonNull
    private final RendererProperties rendererProperties;
    @NonNull
    private final TickProperties tickProperties;
    @NonNull
    private final TacViewerProfiler profiler;
    @NonNull
    private final ScreenComposer screenComposer;
    @NonNull
    private final EngineModuleTemplate engineModule;


    @NonNull
    final FrameBuffer frameBuffer;
    @NonNull
    private final AnimationTimer animationTimerLoop;


    @Setter
    @Nullable
    private ProfileFPSStrategy profileFPSStrategy;


    public TacViewer(
            @NonNull final RendererProperties rendererProperties,
            @NonNull final TickProperties tickProperties,
            @NonNull final ProfilerProvider profilerProvider,
            @NonNull final ScreenComposer screenComposer,
            @NonNull final EngineModuleTemplate engineModule
    ) {
        super(rendererProperties.getWidth(), rendererProperties.getHeight());
        this.rendererProperties = rendererProperties;
        this.tickProperties = tickProperties;
        this.screenComposer = screenComposer;
        this.engineModule = engineModule;

        this.frameBuffer = new FrameBuffer(this, rendererProperties, screenComposer);
        this.profiler = new TacViewerProfiler(profilerProvider);
        this.profiler.startProfiling();

        this.animationTimerLoop = provideAnimationTimer();

        requestFocus();
    }

    @NonNull
    private AnimationTimer provideAnimationTimer() {
        return new AnimationTimer() {
            @Override
            public void handle(final long now) {
                engineLoop(tickProperties, rendererProperties);
                frameBuffer.swap();
                profiler.getJavaFxCounter().countFrame();
            }
        };
    }

    public synchronized void start() {
        engineModule.run();
        animationTimerLoop.start();
    }

    public synchronized void stop() {
        animationTimerLoop.stop();
    }

    private void engineLoop(
            @NonNull final TickProperties tickProperties,
            @NonNull final RendererProperties rendererProperties
    ) {
        profiler.getLoopProfiler().startNextMeasurement();

        // HANDLE INPUT
        engineModule.handleInput();

        // HANDLE TIME
        final long currentNanoTime = System.nanoTime();
        final double elapsedNanoTime = (currentNanoTime - profiler.previousTickNanoTime);
        final long deltaTickNanoTime = currentNanoTime - profiler.previousTickNanoTime;
        final long deltaFrameNanoTime = currentNanoTime - profiler.previousFrameNanoTime;

        // HANDLE UPDATE
        if (deltaTickNanoTime >= tickProperties.getTickThresholdNanoTime()) {
            profiler.previousTickNanoTime = System.nanoTime();
            engineModule.update(elapsedNanoTime);
            profiler.getTpsCounter().countTick();
        }

        // HANDLE RENDER
        if (deltaFrameNanoTime >= rendererProperties.getFrameThresholdNanoTime()) {
            profiler.previousFrameNanoTime = System.nanoTime();
            frameBuffer.backBufferIndex = screenComposer.compose();
            profiler.getFpsCounter().countFrame();
        }

        // HANDLE PROFILING
        profiler.getLoopProfiler().measureLoop();
        if (profileFPSStrategy != null && profiler.getFpsCounter().oneSecondPassed()) {
            profileFPSStrategy.execute(profiler.profileLoop());
        }
    }

}
