package com.recom.tacview.engine;

import com.recom.tacview.engine.graphics.ScreenComposer;
import com.recom.tacview.engine.input.KeyboardInput;
import com.recom.tacview.engine.input.MouseInput;
import com.recom.tacview.property.MetaProperties;
import com.recom.tacview.property.RendererProperties;
import com.recom.tacview.service.InputChannelService;
import com.recom.tacview.service.profiler.FPSCounter;
import com.recom.tacview.service.profiler.Profiler;
import com.recom.tacview.service.profiler.ProfilerProvider;
import com.recom.tacview.service.profiler.TPSCounter;
import com.recom.tacview.service.tick.TickThresholdCalculator;
import com.recom.tacview.service.tick.TickerService;
import com.recom.tacview.strategy.SetJFrameTitleStrategy;
import jakarta.annotation.PostConstruct;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@RequiredArgsConstructor
public class GameLoop extends Canvas implements Runnable {

    // CONSTANTS
    @NonNull
    public static String GAMELOOP_THREAD_NAME = "GLoop";


    // DEPENDENCIES
    @NonNull
    private final RendererProperties rendererProperties;
    @NonNull
    private final MetaProperties metaProperties;
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
    @NonNull
    private final InputChannelService inputChannelService;


    // IMAGE RENDERING
    @Nullable
    private BufferedImage bufferedImage;
    private int[] bufferedImagePixelRaster;


    // EXECUTION THREADS
    @Nullable
    private Thread gameLoopThread;
    @Nullable
    private ExecutorService backBufferExecuter;
    private boolean running = false;


    @Nullable
    private SetJFrameTitleStrategy setJFrameTitleStrategy;


    // INPUT
    @Nullable
    private KeyboardInput keyboardInput;
    @Nullable
    private MouseInput mouseInput;


    @PostConstruct
    public void postConstruct() {

        // CANVAS SIZE
        final Dimension canvasSize = new Dimension(rendererProperties.getScaledWidth(), rendererProperties.getScaledHeight());
        setSize(canvasSize);
        setPreferredSize(canvasSize);
        setMinimumSize(canvasSize);
        setMaximumSize(canvasSize);
        setIgnoreRepaint(true);

        // RENDER BACKBUFFERHANDLER
        if (rendererProperties.getComposer().isParallelizedBackBufferHandler()) {
            backBufferExecuter = Executors.newFixedThreadPool(1);
        }

        // INPUT
//        inputChannelService.getSubject() // @TODO die beiden Inputs werden direkt verdrahtet mit dem service!

        // @TODO AUSLAGERN IN INPUT PROVIDER
        // -> handler statt mit new erzeuge als component; über provider?
        keyboardInput = new KeyboardInput(inputChannelService);
        mouseInput = new MouseInput(inputChannelService);
        addKeyListener(keyboardInput);
        addMouseListener(mouseInput);
        addMouseMotionListener(mouseInput);

        // DISABLE MOUSE CURSOR BY DEFAULT
//        this.setCursor(this.getToolkit().createCustomCursor(
//                new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB), new Point(0, 0),
//                "null"));
    }

    // GAMELOOP RUNNABLE METHODS
    public synchronized void start(@NonNull final SetJFrameTitleStrategy setJFrameTitleStrategy) {
        this.setJFrameTitleStrategy = setJFrameTitleStrategy;

        bufferedImage = new BufferedImage(rendererProperties.getWidth(), rendererProperties.getHeight(), BufferedImage.TYPE_INT_RGB); // @TODO Initialisieren
        bufferedImagePixelRaster = ((DataBufferInt) bufferedImage.getRaster().getDataBuffer()).getData();

        running = true;
        gameLoopThread = new Thread(this, GAMELOOP_THREAD_NAME);
        game.run();
        gameLoopThread.start();
    }

    public synchronized void stop() {
        running = false;
        try {
            gameLoopThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
        System.exit(0);
    }

    @Override
    public void run() {
        requestFocus();
        final Profiler loopProfiler = profilerProvider.provide("Loop");
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

            this.composeGraphics();
            fpsCounter.countFrame();

            loopProfiler.measureLoop();

            if (fpsCounter.oneSecondPassed()) {
                setJFrameTitleStrategy.execute(metaProperties.getName() + " | " + tpsCounter.profileTicksPerSecond() + " | " + fpsCounter.profileFramesPerSecond() + " | " + loopProfiler.stringifyResult());
            }
        }
    }


    // CANVAS / IMAGE-BUFFER HANDLING
    private void composeGraphics() {
        if (rendererProperties.getComposer().isParallelizedBackBufferHandler()) {
            // @TODO -> das sind Strategies!
            composeGraphicsMultithreaded();
        } else {
            // @TODO -> das sind Strategies!
            composeGraphicsSinglethreaded();
        }
    }

    private void composeGraphicsMultithreaded() {
        final BufferStrategy bufferStrategy = this.getBufferStrategy();
        if (bufferStrategy == null) { // @TODO Null check teurer als boolean check?
            createBufferStrategy(3);
            return;
        }

        // Show LAST image in composer buffer
        final Graphics graphicsContext = bufferStrategy.getDrawGraphics();
        graphicsContext.drawImage(bufferedImage, 0, 0, rendererProperties.getScaledWidth(), rendererProperties.getScaledHeight(), null);
        graphicsContext.dispose();
        bufferStrategy.show();

        // Render NEXT frame in buffer
        final int backBufferIndex = screenComposer.compose();

        // Copy NEXT composer image to canvas buffer for showing up in NEXT iteration
        backBufferExecuter.execute(() -> {
            copyComposedBackBufferToCanvasFrontBuffer(backBufferIndex);
        });
    }

    private void composeGraphicsSinglethreaded() {
        final BufferStrategy bufferStrategy = this.getBufferStrategy();
        if (bufferStrategy == null) { // @TODO Null check teurer als boolean check?
            createBufferStrategy(3);
            return;
        }

        final int backBufferIndex = screenComposer.compose();
        copyComposedBackBufferToCanvasFrontBuffer(backBufferIndex);

        final Graphics graphicsContext = bufferStrategy.getDrawGraphics();
        graphicsContext.drawImage(bufferedImage, 0, 0, rendererProperties.getScaledWidth(), rendererProperties.getScaledHeight(), null);
        graphicsContext.dispose();
        bufferStrategy.show();
    }

    private void copyComposedBackBufferToCanvasFrontBuffer(int backBufferIndex) {
        System.arraycopy(screenComposer.getBackPixelBuffer(backBufferIndex).directBufferAccess(), 0, bufferedImagePixelRaster, 0, screenComposer.getBackPixelBuffer(backBufferIndex).getBufferSize() - 1);
    }

}
