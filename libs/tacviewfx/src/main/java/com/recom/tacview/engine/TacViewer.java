package com.recom.tacview.engine;

import com.recom.commons.units.ResizeCommand;
import com.recom.commons.units.TimeUnits;
import com.recom.observer.Notification;
import com.recom.observer.ReactiveObserver;
import com.recom.observer.Subjective;
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
import com.recom.tacview.util.ResizeCommandFactory;
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
    private final FlippableCanvasBufferController canvasBuffer;
    @NonNull
    private final AnimationTimer animationTimerLoop;
    @NonNull
    private final Thread engineLoopThread;
    @Setter
    @NonNull
    private Optional<ProfileFPSStrategy> maybeProfileFPSStrategy;
    @NonNull
    final ReactiveObserver<IsEngineProperties> enginePropertiesReactiveObserver;
    boolean resizeBuffer = false;


    public TacViewer(
            @NonNull final IsEngineProperties engineProperties,
            @NonNull final ProfilerProvider profilerProvider,
            @NonNull final ScreenComposer screenComposer,
            @NonNull final EngineModule engineModule,
            @NonNull final GenericFXInputEventListener genericFXInputEventListener,
            @NonNull final InputManager inputManager,
            @NonNull final Thread.UncaughtExceptionHandler globalExceptionHandler
    ) {
        super();
        this.engineProperties = engineProperties;
        this.screenComposer = screenComposer;
        this.engineModule = engineModule;
        this.genericFXInputEventListener = genericFXInputEventListener;
        this.inputManager = inputManager;
        this.globalExceptionHandler = globalExceptionHandler;

        this.canvasBuffer = new FlippableCanvasBufferController(this, engineProperties, screenComposer);
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

        enginePropertiesReactiveObserver = registerFrameResizeReactiveObserver(engineProperties);
    }

    @NonNull
    private ReactiveObserver<IsEngineProperties> registerFrameResizeReactiveObserver(@NonNull final IsEngineProperties engineProperties) {
        final ReactiveObserver<IsEngineProperties> enginePropertiesReactiveObserver = ReactiveObserver.reactWith((
                @NonNull final Subjective<IsEngineProperties> __,
                @NonNull final Notification<IsEngineProperties> notification
        ) -> {
            final IsEngineProperties properties = notification.getPayload();
            if (properties.getRendererHeight() == 0 || properties.getRendererWidth() == 0) {
                // prevent issues during initialization? test log ...
                log.error("Renderer width or height is 0, cannot resize canvas buffer");
            } else {
                resizeBuffer = true;
            }
        });
        enginePropertiesReactiveObserver.observe(engineProperties.getBufferedSubject());

        return enginePropertiesReactiveObserver;
    }

    @NonNull
    private AnimationTimer provideAnimationTimer() {
        return new AnimationTimer() {
            @Override
            public void handle(final long now) {
                canvasBuffer.flip();
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

        // HANDLE BUFFER RESIZE
        if (resizeBuffer) {
            handleResize(engineProperties);
        }

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

    private void handleResize(@NonNull final IsEngineProperties engineProperties) {
        animationTimerLoop.stop();
        final ResizeCommand resizeCommand = ResizeCommandFactory.createResizeCommand(engineProperties);

        screenComposer.resizeBuffer(resizeCommand);
        canvasBuffer.resizeBuffer(resizeCommand);

        engineModule.getEnvironment().getRenderPipeline().updateLayers();
        engineModule.getEnvironment().getRenderPipeline().setDirty(true); // unnecessary because updateLayers() already sets it to true

        resizeBuffer = false;
        animationTimerLoop.start();
    }

}
