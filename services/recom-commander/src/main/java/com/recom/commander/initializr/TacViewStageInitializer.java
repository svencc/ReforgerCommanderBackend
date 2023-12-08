package com.recom.commander.initializr;

import com.recom.commander.event.ShutdownEvent;
import com.recom.commander.event.StageReadyEvent;
import com.recom.commander.property.SpringApplicationProperties;
import com.recom.commander.service.gateway.MapTopographyGateway;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;


@Slf4j
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

    @NonNull
    private final MapTopographyGateway mapTopographyGateway;


    private Stage stage = null;
    private TacViewer tacViewer = null;
    private AnimationTimer bufferToCanvasUpdaterLoop = null;

    @Override
    public void onApplicationEvent(@NonNull final StageReadyEvent event) {
        log.info("Initializing TacView ...");
        final Stage tacViewStage = event.getStage();
        populateTacViewStage(tacViewStage);
        tacViewStage.show();
    }

    private void populateTacViewStage(@NonNull final Stage canvasStage) {
        this.stage = canvasStage;
        final BorderPane root = new BorderPane();
        root.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));

        tacViewer = new TacViewer(
                rendererProperties,
                profilerProvider,
                tickThresholdCalculator,
                tickerService,
                screenComposer,
                game
        );
        root.setCenter(tacViewer);
        tacViewer.start();

        tacViewer.setOnMouseClicked(event -> {
            mapTopographyGateway.provideMapTopographyData();
        });

        final Scene scene = new Scene(root, rendererProperties.getWidth(), rendererProperties.getHeight());
        canvasStage.setTitle(springApplicationProperties.getName());
        canvasStage.setResizable(false);
        canvasStage.setScene(scene);
    }

    @EventListener(classes = ShutdownEvent.class)
    public void shutdown() {
        log.warn("Shutdown TacView ...");
        tacViewer.stop();
    }

}

//      https://www.youtube.com/watch?v=UDNrJAvKc0k https://youtu.be/UDNrJAvKc0k?si=GDu7RdGS3CeVM0rc&t=819
//      fetch data with new REST Client