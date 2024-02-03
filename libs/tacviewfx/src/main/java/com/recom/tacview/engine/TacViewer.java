package com.recom.tacview.engine;

import com.recom.tacview.engine.graphics.ScreenComposer;
import com.recom.tacview.engine.input.GenericFXInputEventListener;
import com.recom.tacview.engine.input.InputManager;
import com.recom.tacview.engine.input.mapper.keyboard.JavaFxKeyboardCommandMapper;
import com.recom.tacview.engine.input.mapper.mousebutton.JavaFxMouseButtonCommandMapper;
import com.recom.tacview.engine.input.mapper.scroll.JavaFxMouseScrollCommandMapper;
import com.recom.tacview.engine.module.EngineModule;
import com.recom.tacview.property.RendererProperties;
import com.recom.tacview.property.TickProperties;
import com.recom.tacview.service.profiler.ProfilerProvider;
import com.recom.tacview.strategy.ProfileFPSStrategy;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.InputEvent;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;

import java.time.Duration;

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
    private final Thread.UncaughtExceptionHandler globalExceptionHandler;


    @NonNull
    private final SwappableCanvasBuffer canvasBuffer;
    @NonNull
    private Thread engineLoopRunnerThread;

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
            @NonNull final InputManager inputManager,
            @NonNull final Thread.UncaughtExceptionHandler globalExceptionHandler
    ) {
        super(rendererProperties.getScaledWindowWidth(), rendererProperties.getScaledWindowHeight());
        this.rendererProperties = rendererProperties;
        this.tickProperties = tickProperties;
        this.screenComposer = screenComposer;
        this.engineModule = engineModule;
        this.genericFXInputEventListener = genericFXInputEventListener;
        this.inputManager = inputManager;
        this.globalExceptionHandler = globalExceptionHandler;

        this.canvasBuffer = new SwappableCanvasBuffer(this, rendererProperties, screenComposer);
        this.profiler = new TacViewerProfiler(profilerProvider);
        this.profiler.startProfiling();

        this.setFocusTraversable(true);
        this.requestFocus();

        // register input event listener
        this.setEventHandler(InputEvent.ANY, this.genericFXInputEventListener);
        this.inputManager.registerCommandMapper(new JavaFxMouseButtonCommandMapper());
        this.inputManager.registerCommandMapper(new JavaFxMouseScrollCommandMapper());
        this.inputManager.registerCommandMapper(new JavaFxKeyboardCommandMapper());
    }

    private void runEngineLoop() {
        engineLoopRunnerThread = new Thread(() -> {
            Thread.currentThread().setUncaughtExceptionHandler(globalExceptionHandler);
            Thread.setDefaultUncaughtExceptionHandler(globalExceptionHandler);

            while (!Thread.currentThread().isInterrupted()) {
                engineLoop(tickProperties, rendererProperties);
                Platform.runLater(canvasBuffer::swap);
                profiler.getLoopCounter().countLoop();
            }
        });

        engineLoopRunnerThread.setDaemon(false);
        engineLoopRunnerThread.start();
    }


    public synchronized void start() {
        engineModule.run();
        runEngineLoop();
    }

    public synchronized void stop() {
        engineLoopRunnerThread.interrupt();
        try {
            Thread.sleep(Duration.ofSeconds(1));
        } catch (InterruptedException e) {
            log.error("Interrupted while waiting for engine loop to stop", e);
        }
    }

    private void engineLoop(
            @NonNull final TickProperties tickProperties,
            @NonNull final RendererProperties rendererProperties
    ) {
        long targetNanosBetweenLoops = Math.max(tickProperties.getTickThresholdNanoTime(), rendererProperties.getFrameThresholdNanoTime());

        // HANDLE TIME
        final long currentNanoTime = System.nanoTime();
        final long elapsedEngineNanoTime = (currentNanoTime - profiler.previousTickNanoTime);
        final long deltaTickNanoTime = currentNanoTime - profiler.previousTickNanoTime;
        final long deltaFrameNanoTime = currentNanoTime - profiler.previousFrameNanoTime;

        final long potentialNanosToSleep = targetNanosBetweenLoops - elapsedEngineNanoTime;
        if (potentialNanosToSleep > 0) {
            try {
                Thread.sleep(potentialNanosToSleep / 1_000_000, (int) (potentialNanosToSleep % 1_000_000));
            } catch (InterruptedException e) {
                log.error("Interrupted engineLoop while sleeping", e);
            }
        }


        // HANDLE INPUT
        final long inputHandlingStart = System.nanoTime();
        inputManager.mapInputEventsToCommands();
        engineModule.handleInputCommands(inputManager.popInputCommands());
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
            screenComposer.compose(engineModule.getEnvironment());
            profiler.getFpsCounter().countFrame();
        }

        // HANDLE PROFILING
        if (profileFPSStrategy != null && profiler.getLoopCounter().isOneSecondPassed()) {
            profileFPSStrategy.execute(profiler.writeProfile());
        }
    }

}
