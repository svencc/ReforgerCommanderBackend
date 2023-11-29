package com.recom.tacview.engine;

import com.recom.tacview.engine.graphics.ScreenComposer;
import com.recom.tacview.engine.input.KeyboardInput;
import com.recom.tacview.engine.input.MouseInput;
import com.recom.tacview.property.RendererProperties;
import com.recom.tacview.service.profiler.FPSCounter;
import com.recom.tacview.service.profiler.Profiler;
import com.recom.tacview.service.profiler.ProfilerProvider;
import com.recom.tacview.service.profiler.TPSCounter;
import com.recom.tacview.service.tick.TickThresholdCalculator;
import com.recom.tacview.service.tick.TickerService;
import com.recom.tacview.strategy.SetFPSStrategy;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelBuffer;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import lombok.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.nio.IntBuffer;
import java.util.concurrent.ExecutorService;

@Component
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
    private final GameTemplate game;
//    @NonNull
//    private final InputChannelService inputChannelService;


    // IMAGE RENDERING BUFFER
    private IntBuffer intBuffer = null;
    private PixelFormat<IntBuffer> pixelFormat = null;
    private PixelBuffer<IntBuffer> pixelBuffer = null;
    private WritableImage img = null;
    private int backBufferIndex = 0;


    // EXECUTION THREADS
    @Nullable
    private Thread renderLoopThread;
    private boolean running = false;


    @Nullable
    private SetFPSStrategy setFPSStrategy;


    public TacViewer(
            @NonNull final RendererProperties rendererProperties,
            @NonNull final ProfilerProvider profilerProvider,
            @NonNull final TickThresholdCalculator tickThresholdCalculator,
            @NonNull final TickerService tickerService,
            @NonNull final ScreenComposer screenComposer,
            @NonNull final GameTemplate game
    ) {
        // CANVAS SIZE
        super(rendererProperties.getWidth(), rendererProperties.getHeight());
        this.rendererProperties = rendererProperties;
        this.profilerProvider = profilerProvider;
        this.tickThresholdCalculator = tickThresholdCalculator;
        this.tickerService = tickerService;
        this.screenComposer = screenComposer;
        this.game = game;

        intBuffer = IntBuffer.allocate(rendererProperties.getWidth() * rendererProperties.getHeight());
        pixelFormat = PixelFormat.getIntArgbPreInstance();
        pixelBuffer = new PixelBuffer<>(rendererProperties.getWidth(), rendererProperties.getHeight(), intBuffer, pixelFormat);
        img = new WritableImage(pixelBuffer);


//        setSize(canvasSize);
//        setPreferredSize(canvasSize);
//        setMinimumSize(canvasSize);
//        setMaximumSize(canvasSize);
//        setIgnoreRepaint(true);

//        // RENDER BACKBUFFERHANDLER
//        if (rendererProperties.getComposer().isParallelizedBackBufferHandler()) {
//            backBufferExecutor = Executors.newFixedThreadPool(1);
//        }
    }


    // GAMELOOP RUNNABLE METHODS
    public synchronized void start() {
        // Create IMAGE BUFFER

        // Start Loop
        running = true;
        renderLoopThread = new Thread(this, THREAD_NAME);
        game.run();
        renderLoopThread.start();
    }

    public synchronized void stop() {
        running = false;
        try {
            renderLoopThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
        System.exit(0);
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

            if (fpsCounter.oneSecondPassed()) {
                // TODO: setFPS into stage title .... generic
//                setFPSStrategy.execute(() ->);
//                setFPSStrategy.execute(metaProperties.getName() + " | " + tpsCounter.profileTicksPerSecond() + " | " + fpsCounter.profileFramesPerSecond() + " | " + loopProfiler.stringifyResult());
            }
        }
    }


    // CANVAS / IMAGE-BUFFER HANDLING
    private void composeGraphics() {
        // Render NEXT frame to next backBuffer
        backBufferIndex = screenComposer.compose();
    }

    public void copyComposedBackBufferToCanvasFrontBuffer() {
        pixelBuffer.getBuffer().put(screenComposer.getBackPixelBuffer(backBufferIndex).directBufferAccess());
        pixelBuffer.getBuffer().flip();
        pixelBuffer.updateBuffer(__ -> null);
        getGraphicsContext2D().drawImage(img, 0, 0);
    }

}
