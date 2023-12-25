package com.recom.tacview.engine;

import com.recom.tacview.engine.graphics.ScreenComposer;
import com.recom.tacview.engine.module.EngineModuleTemplate;
import com.recom.tacview.property.RendererProperties;
import com.recom.tacview.service.profiler.FPSCounter;
import com.recom.tacview.service.profiler.Profiler;
import com.recom.tacview.service.profiler.ProfilerProvider;
import com.recom.tacview.service.profiler.TPSCounter;
import com.recom.tacview.service.tick.TickThresholdCalculator;
import com.recom.tacview.service.tick.TickerService;
import com.recom.tacview.strategy.ProfileFPSStrategy;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.PixelBuffer;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.nio.IntBuffer;

@Slf4j
//@Component
public class TacViewer extends Canvas implements Runnable {

    // CONSTANTS
    @NonNull
    public static String THREAD_NAME = "TacViewer";


    // DEPENDENCIES
    @NonNull
    private final RendererProperties rendererProperties;
    @NonNull
    private final ProfilerProvider profilerProvider;
    @NonNull
    private final TickThresholdCalculator tickThresholdCalculator;
    @NonNull
    private final TickerService tickerService;
    @NonNull
    private final ScreenComposer screenComposer;
    @NonNull
    private final EngineModuleTemplate engineFlavour;


    // IMAGE RENDERING BUFFER
    private IntBuffer intBuffer = null;
    private PixelFormat<IntBuffer> pixelFormat = null;
    private PixelBuffer<IntBuffer> pixelBuffer = null;
    private WritableImage img = null;
    private int backBufferIndex = 0;


    // EXECUTION THREADS
    @Nullable
    private Thread renderLoopThread = null;
    private AnimationTimer bufferToCanvasUpdaterLoop = null;
    private volatile boolean running = false;


    @Setter
    @Nullable
    private ProfileFPSStrategy profileFPSStrategy;


    public TacViewer(
            @NonNull final RendererProperties rendererProperties,
            @NonNull final ProfilerProvider profilerProvider,
            @NonNull final TickThresholdCalculator tickThresholdCalculator,
            @NonNull final TickerService tickerService,
            @NonNull final ScreenComposer screenComposer,
            @NonNull final EngineModuleTemplate engineFlavour
    ) {
        // DEPENDENCIES
        super(rendererProperties.getWidth(), rendererProperties.getHeight());
        this.rendererProperties = rendererProperties;
        this.profilerProvider = profilerProvider;
        this.tickThresholdCalculator = tickThresholdCalculator;
        this.tickerService = tickerService;
        this.screenComposer = screenComposer;
        this.engineFlavour = engineFlavour;

        // CANVAS IMAGE BUFFER
        intBuffer = IntBuffer.allocate(rendererProperties.getWidth() * rendererProperties.getHeight());
        pixelFormat = PixelFormat.getIntArgbPreInstance();
        pixelBuffer = new PixelBuffer<>(rendererProperties.getWidth(), rendererProperties.getHeight(), intBuffer, pixelFormat);
        img = new WritableImage(pixelBuffer);

        // JAVA FX AnimationTimer
        bufferToCanvasUpdaterLoop = new AnimationTimer() {
            @Override
            public void handle(final long now) {
                copyComposedBackBufferToCanvasFrontBuffer();
            }
        };
    }

    private void copyComposedBackBufferToCanvasFrontBuffer() {
        pixelBuffer.getBuffer().put(screenComposer.getBackPixelBuffer(backBufferIndex).directBufferAccess());
        pixelBuffer.getBuffer().flip();
        pixelBuffer.updateBuffer(__ -> null);
        getGraphicsContext2D().drawImage(img, 0, 0);
    }

    // GAMELOOP RUNNABLE METHODS
    public synchronized void start() {
        // Start Loop
        this.running = true;
        renderLoopThread = new Thread(this, THREAD_NAME);
        engineFlavour.run();
        renderLoopThread.start();
        bufferToCanvasUpdaterLoop.start();
    }

    public synchronized void stop() {
        running = false;
        bufferToCanvasUpdaterLoop.stop();
        try {
            renderLoopThread.join();
        } catch (final InterruptedException e) {
            log.error("Error while stopping TacViewer", e);
        }
    }

    @Override
    public void run() {
        requestFocus();
        final Profiler loopProfiler = profilerProvider.provide("TacViewerLoop");
        final FPSCounter fpsCounter = profilerProvider.provideFPSCounter();
        final TPSCounter tpsCounter = profilerProvider.provideTPSCounter();
        fpsCounter.startProfiling();
        double tickThresholdRatio = 0;

        while (running) {
            loopProfiler.startNextMeasurement();
            tickThresholdRatio += tickThresholdCalculator.calculate(loopProfiler.getProfiledNanos());

            while (tickThresholdRatio >= 1.0) {
                tickerService.tick();
                tpsCounter.countTick();
                tickThresholdRatio--;
            }

            composeGraphics();
            fpsCounter.countFrame();
            loopProfiler.measureLoop();

            if (fpsCounter.oneSecondPassed() && profileFPSStrategy != null) {
                final String profiled = String.format("%1s | %2s | %3s", tpsCounter.profileTicksPerSecond(), fpsCounter.profileFramesPerSecond(), loopProfiler.stringifyResult());
                profileFPSStrategy.execute(profiled);
                log.info(profiled);
            }
        }
    }

    // CANVAS / IMAGE-BUFFER HANDLING
    private void composeGraphics() {
        // Render NEXT frame to next backBuffer
        backBufferIndex = screenComposer.compose();
    }

}
