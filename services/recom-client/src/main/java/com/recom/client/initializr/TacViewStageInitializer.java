package com.recom.client.initializr;

import com.recom.client.event.StageReadyEvent;
import com.recom.client.property.SpringApplicationProperties;
import com.recom.tacview.engine.GameTemplate;
import com.recom.tacview.engine.TacViewer;
import com.recom.tacview.engine.graphics.ScreenComposer;
import com.recom.tacview.property.RendererProperties;
import com.recom.tacview.service.profiler.ProfilerProvider;
import com.recom.tacview.service.tick.TickThresholdCalculator;
import com.recom.tacview.service.tick.TickerService;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class TacViewStageInitializer implements ApplicationListener<StageReadyEvent> {

    @NonNull
    private final SpringApplicationProperties springApplicationProperties;
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

    private AnimationTimer bufferToCanvasUpdaterLoop = null;

    @Override
    public void onApplicationEvent(@NonNull final StageReadyEvent event) {
        final Stage tacViewStage = event.getStage();
        populateTacViewStage(tacViewStage);
        tacViewStage.show();
    }

    private void populateTacViewStage(@NonNull final Stage canvasStage) {
        final BorderPane root = new BorderPane();
//        root.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));

        final TacViewer tacViewer = new TacViewer(
                rendererProperties,
                profilerProvider,
                tickThresholdCalculator,
                tickerService,
                screenComposer,
                game
        );

        root.setCenter(tacViewer);
        tacViewer.start();

        bufferToCanvasUpdaterLoop = new AnimationTimer() {
            @Override
            public void handle(final long now) {
                tacViewer.copyComposedBackBufferToCanvasFrontBuffer();
            }
        };
        bufferToCanvasUpdaterLoop.start();

        final Scene scene = new Scene(root, rendererProperties.getWidth(), rendererProperties.getHeight());
        canvasStage.setTitle(springApplicationProperties.getName());
        canvasStage.setResizable(false);
        canvasStage.setScene(scene);
    }

}

//      https://www.youtube.com/watch?v=UDNrJAvKc0k https://youtu.be/UDNrJAvKc0k?si=GDu7RdGS3CeVM0rc&t=819
//      fetch data with new REST Client