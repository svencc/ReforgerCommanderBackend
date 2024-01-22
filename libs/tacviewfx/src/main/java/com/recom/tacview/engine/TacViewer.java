package com.recom.tacview.engine;

import com.recom.tacview.engine.graphics.ScreenComposer;
import com.recom.tacview.engine.input.GenericFXInputEventListener;
import com.recom.tacview.engine.input.InputManager;
import com.recom.tacview.engine.input.command.mapper.mouse.JavaFxMouseCommandMapper;
import com.recom.tacview.engine.module.EngineModule;
import com.recom.tacview.property.RendererProperties;
import com.recom.tacview.property.TickProperties;
import com.recom.tacview.service.profiler.ProfilerProvider;
import com.recom.tacview.strategy.ProfileFPSStrategy;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.InputEvent;
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
    private final EngineModule engineModule;
    @NonNull
    private final GenericFXInputEventListener genericFXInputEventListener;
    @NonNull
    private final InputManager inputManager;


    @NonNull
    private final CanvasBufferSwapCommand canvasBuffer;
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
            @NonNull final EngineModule engineModule,
            @NonNull final GenericFXInputEventListener genericFXInputEventListener,
            @NonNull final InputManager inputManager
    ) {
        super(rendererProperties.getWidth(), rendererProperties.getHeight());
        this.rendererProperties = rendererProperties;
        this.tickProperties = tickProperties;
        this.screenComposer = screenComposer;
        this.engineModule = engineModule;
        this.genericFXInputEventListener = genericFXInputEventListener;
        this.inputManager = inputManager;

        this.canvasBuffer = new CanvasBufferSwapCommand(this, rendererProperties, screenComposer);
        this.profiler = new TacViewerProfiler(profilerProvider);
        this.profiler.startProfiling();

        this.animationTimerLoop = provideAnimationTimer();

        this.setFocusTraversable(true);
        this.requestFocus();

        // register input event listener
        this.setEventHandler(InputEvent.ANY, this.genericFXInputEventListener);
        this.inputManager.registerCommandMapper(new JavaFxMouseCommandMapper());
    }

    @NonNull
    private AnimationTimer provideAnimationTimer() {
        return new AnimationTimer() {
            @Override
            public void handle(final long now) {
                engineLoop(tickProperties, rendererProperties);
                canvasBuffer.swap();
                profiler.getLoopCounter().countLoop();
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
        // HANDLE TIME
        final long currentNanoTime = System.nanoTime();
        final long elapsedEngineNanoTime = (currentNanoTime - profiler.previousTickNanoTime);
        final long deltaTickNanoTime = currentNanoTime - profiler.previousTickNanoTime;
        final long deltaFrameNanoTime = currentNanoTime - profiler.previousFrameNanoTime;

        // HANDLE INPUT
        final long inputHandlingStart = System.nanoTime();
        inputManager.mapInputEventsToCommands();
        engineModule.handleInputCommands(inputManager.popInputCommands());
        inputManager.clearInputQueues();
        profiler.inputHandlingNanoTime = System.nanoTime() - inputHandlingStart;

        // HANDLE UPDATE
        if (deltaTickNanoTime >= tickProperties.getTickThresholdNanoTime()) {
            profiler.previousTickNanoTime = System.nanoTime();
            engineModule.update(elapsedEngineNanoTime);
            profiler.getTpsCounter().countTick();
        }

        // HANDLE RENDER
        if (deltaFrameNanoTime >= rendererProperties.getFrameThresholdNanoTime()) {
            profiler.previousFrameNanoTime = System.nanoTime();
            canvasBuffer.currentBackBufferIndex = screenComposer.compose(engineModule.getEnvironment());
            profiler.getFpsCounter().countFrame();
        }

        // HANDLE PROFILING
        if (profileFPSStrategy != null && profiler.getLoopCounter().isOneSecondPassed()) {
            profileFPSStrategy.execute(profiler.writeProfile());
        }
    }

}
