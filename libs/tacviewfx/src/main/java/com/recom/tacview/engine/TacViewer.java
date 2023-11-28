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
import jakarta.annotation.PostConstruct;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.PixelBuffer;
import javafx.scene.image.PixelFormat;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.nio.IntBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@RequiredArgsConstructor
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


    // EXECUTION THREADS
    @Nullable
    private Thread renderLoopThread;
    @Nullable
    private ExecutorService backBufferExecutor;
    private boolean running = false;


    @Nullable
    private SetFPSStrategy setFPSStrategy;


    // INPUT
    @Nullable
    private KeyboardInput keyboardInput;
    @Nullable
    private MouseInput mouseInput;


    @PostConstruct
    public void postConstruct() {
        // CANVAS SIZE
        final Dimension canvasSize = new Dimension(rendererProperties.getWidth(), rendererProperties.getHeight());
//        setSize(canvasSize);
//        setPreferredSize(canvasSize);
//        setMinimumSize(canvasSize);
//        setMaximumSize(canvasSize);
//        setIgnoreRepaint(true);

        // RENDER BACKBUFFERHANDLER
        if (rendererProperties.getComposer().isParallelizedBackBufferHandler()) {
            backBufferExecutor = Executors.newFixedThreadPool(1);
        }
    }


    // GAMELOOP RUNNABLE METHODS
    public synchronized void start() {
        // Create IMAGE BUFFER
        intBuffer = IntBuffer.allocate(rendererProperties.getWidth() * rendererProperties.getHeight());
        pixelFormat = PixelFormat.getIntArgbPreInstance();
        pixelBuffer = new PixelBuffer<>(rendererProperties.getWidth(), rendererProperties.getHeight(), intBuffer, pixelFormat);

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
        //@TODO: prepare a runnable; instead having to execute this IF each frame
        if (rendererProperties.getComposer().isParallelizedBackBufferHandler()) {
            // @TODO -> das sind Strategies!
            composeGraphicsMultithreaded();
        } else {
            // @TODO -> das sind Strategies!
            composeGraphicsSinglethreaded();
        }
    }

    private void composeGraphicsMultithreaded() {
        // Render NEXT frame in buffer
        final int backBufferIndex = screenComposer.compose();

        // Copy NEXT composer image to canvas buffer for showing up in NEXT iteration
        backBufferExecutor.execute(() -> {
            copyComposedBackBufferToCanvasFrontBuffer(backBufferIndex);
        });
    }

    private void composeGraphicsSinglethreaded() {
        // Render NEXT frame in buffer
        final int backBufferIndex = screenComposer.compose();
        copyComposedBackBufferToCanvasFrontBuffer(backBufferIndex);
    }

    private void copyComposedBackBufferToCanvasFrontBuffer(final int backBufferIndex) {
        pixelBuffer.getBuffer().put(screenComposer.getBackPixelBuffer(backBufferIndex).directBufferAccess());
        pixelBuffer.getBuffer().flip();
        pixelBuffer.updateBuffer(__ -> null);
    }

}
