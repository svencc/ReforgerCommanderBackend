package com.recom.tacview.engine;

import com.recom.commons.units.TimeUnits;
import com.recom.tacview.engine.graphics.ScreenComposer;
import com.recom.tacview.engine.input.GenericFXInputEventListener;
import com.recom.tacview.engine.input.InputManager;
import com.recom.tacview.engine.input.mapper.keyboard.JavaFxKeyboardCommandMapper;
import com.recom.tacview.engine.input.mapper.mousebutton.JavaFxMouseButtonCommandMapper;
import com.recom.tacview.engine.input.mapper.scroll.JavaFxMouseScrollCommandMapper;
import com.recom.tacview.engine.module.EngineModule;
import com.recom.tacview.property.IsEngineProperties;
import com.recom.tacview.service.profiler.ProfilerProvider;
import com.recom.tacview.strategy.ProfileFPSStrategy;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.InputEvent;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Optional;

@Slf4j
public class TacViewer extends Canvas {

    @NonNull
    private final IsEngineProperties engineProperties;
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
    private final AnimationTimer animationTimerLoop;
    @NonNull
    private final Thread engineLoopThread;
    @Setter
    @NonNull
    private Optional<ProfileFPSStrategy> maybeProfileFPSStrategy;


    public TacViewer(
            @NonNull final IsEngineProperties engineProperties,
            @NonNull final ProfilerProvider profilerProvider,
            @NonNull final ScreenComposer screenComposer,
            @NonNull final EngineModule engineModule,
            @NonNull final GenericFXInputEventListener genericFXInputEventListener,
            @NonNull final InputManager inputManager,
            @NonNull final Thread.UncaughtExceptionHandler globalExceptionHandler
    ) {
        super(engineProperties.getScaledWindowWidth(), engineProperties.getScaledWindowHeight());
        this.engineProperties = engineProperties;
        this.screenComposer = screenComposer;
        this.engineModule = engineModule;
        this.genericFXInputEventListener = genericFXInputEventListener;
        this.inputManager = inputManager;
        this.globalExceptionHandler = globalExceptionHandler;

        this.canvasBuffer = new SwappableCanvasBuffer(this, engineProperties, screenComposer);
        this.profiler = new TacViewerProfiler(profilerProvider);
        this.profiler.startProfiling();

        this.animationTimerLoop = provideAnimationTimer();
        this.engineLoopThread = provideEngineLoopThread();

        this.setFocusTraversable(true);
        this.requestFocus();

        // register input event listener
        this.setEventHandler(InputEvent.ANY, this.genericFXInputEventListener);
        this.inputManager.registerCommandMapper(new JavaFxMouseButtonCommandMapper());
        this.inputManager.registerCommandMapper(new JavaFxMouseScrollCommandMapper());
        this.inputManager.registerCommandMapper(new JavaFxKeyboardCommandMapper());
    }

    @NonNull
    private AnimationTimer provideAnimationTimer() {
        return new AnimationTimer() {
            @Override
            public void handle(final long now) {
                canvasBuffer.swap();
            }
        };
    }


    @NonNull
    private Thread provideEngineLoopThread() {
        final Thread thread = new Thread(() -> {
            Thread.currentThread().setUncaughtExceptionHandler(globalExceptionHandler);
            Thread.setDefaultUncaughtExceptionHandler(globalExceptionHandler);

            while (!Thread.currentThread().isInterrupted()) {
                engineLoop(engineProperties);
                profiler.getLoopCounter().countLoop();
            }
        });
        thread.setDaemon(false);

        return thread;
    }

    public synchronized void start() {
        engineModule.run();
        animationTimerLoop.start();
        engineLoopThread.start();
    }

    public synchronized void stop() {
        animationTimerLoop.stop();
        engineLoopThread.interrupt();
        try {
            Thread.sleep(Duration.ofMillis(10));
        } catch (final InterruptedException e) {
            log.error("Interrupted while waiting for engine loop to stop");
        }
    }

    private void engineLoop(@NonNull final IsEngineProperties engineProperties) {
        // HANDLE TIME CALCULATIONS
        final long currentNanoTimeOnLoopStart = System.nanoTime();
        final long elapsedEngineNanoTime = (currentNanoTimeOnLoopStart - profiler.previousTickNanoTime);
        final long deltaTickNanoTime = currentNanoTimeOnLoopStart - profiler.previousTickNanoTime;
        final long deltaFrameNanoTime = currentNanoTimeOnLoopStart - profiler.previousFrameNanoTime;
        final long targetNanosOfLoops = Math.max(engineProperties.getTickThresholdNanoTime(), engineProperties.getFrameThresholdNanoTime());

        // HANDLE INPUT
        final long inputHandlingStart = System.nanoTime();
        inputManager.mapInputEventsToCommands();
        engineModule.handleInputCommands(inputManager.popInputCommands());
        profiler.inputHandlingNanoTime = System.nanoTime() - inputHandlingStart;

        // HANDLE UPDATE
        if (deltaTickNanoTime >= engineProperties.getTickThresholdNanoTime()) {
            profiler.previousTickNanoTime = System.nanoTime();
            engineModule.update(elapsedEngineNanoTime);
            profiler.getTpsCounter().countTick();
        }

        // HANDLE RENDER
        if (deltaFrameNanoTime >= engineProperties.getFrameThresholdNanoTime()) {
            profiler.previousFrameNanoTime = System.nanoTime();
            screenComposer.compose(engineModule.getEnvironment());
            profiler.getFpsCounter().countFrame();
        }

        // HANDLE PROFILING
        if (maybeProfileFPSStrategy.isPresent() && profiler.getLoopCounter().isOneSecondPassed()) {
            maybeProfileFPSStrategy.get().execute(profiler.writeProfile());
        }

        // SLEEP UNTIL NEXT LOOP
        final long loopNanosUntilHere = System.nanoTime() - currentNanoTimeOnLoopStart;
        final long nanosToSleep = targetNanosOfLoops - loopNanosUntilHere;
        if (nanosToSleep >= 0) {
            try {
                Thread.sleep(nanosToSleep / TimeUnits.ONE_MILLI_IN_NANOS_L, (int) (nanosToSleep % TimeUnits.ONE_MILLI_IN_NANOS_L));
            } catch (final InterruptedException e) {
                log.warn("Interrupted engineLoop while sleeping");
            }
        }
    }

}
