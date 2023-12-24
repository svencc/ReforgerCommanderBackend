package com.recom.commander.initializr;

import com.recom.commander.event.InitializeStageEvent;
import com.recom.commander.event.ShutdownEvent;
import com.recom.commander.property.SpringApplicationProperties;
import com.recom.tacview.engine.EngineModuleTemplate;
import com.recom.tacview.engine.TacViewer;
import com.recom.tacview.engine.graphics.ScreenComposer;
import com.recom.tacview.property.RendererProperties;
import com.recom.tacview.service.profiler.ProfilerProvider;
import com.recom.tacview.service.tick.TickThresholdCalculator;
import com.recom.tacview.service.tick.TickerService;
import com.recom.tacview.strategy.ProfileFPSStrategy;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class TacViewStageInitializer {

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
    private final EngineModuleTemplate game;
    //@NonNull
    //private final MapTopographyDataService mapTopographyDataService;

    @Nullable
    private TacViewer tacViewer = null;


    @EventListener(classes = InitializeStageEvent.class)
    public void onApplicationEvent(@NonNull final InitializeStageEvent event) {
        event.logStageInitializationWithMessage(log, TacViewStageInitializer.class, "Starting TacView");
        final Stage tacViewStage = event.getStage();
        populateTacViewStage(tacViewStage);
        tacViewStage.show();
    }

    private void populateTacViewStage(@NonNull final Stage canvasStage) {
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

        final Scene scene = new Scene(root, rendererProperties.getWidth(), rendererProperties.getHeight());
        canvasStage.setTitle(springApplicationProperties.getName());
        final StringProperty titleProperty = canvasStage.titleProperty();

        final ProfileFPSStrategy profileFPSStrategy = new ProfileFPSStrategy((@NonNull final String profiled) -> {
            Platform.runLater(() -> titleProperty.setValue(profiled));
        });
        tacViewer.setProfileFPSStrategy(profileFPSStrategy);
        tacViewer.start();

        /*
        // @TODO: Remove this method call; DEV ONLY
        // __initDevMouseHandler();
        final StringProperty stringProperty = canvasStage.titleProperty();
        stringProperty.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                canvasStage.setTitle(newValue);
            }
        });
        __initDevTitleHandler(stringProperty);
        */// ---------------------------------------------

        canvasStage.setResizable(false);
        canvasStage.setScene(scene);
    }

    // @TODO: Remove this method; DEV ONLY
    private void __initDevTitleHandler(@NonNull final StringProperty stringProperty) {
        tacViewer.setOnMouseClicked(event -> {
            stringProperty.setValue(String.valueOf(Math.random()));
        });
    }

    // @TODO: Remove this method; DEV ONLY
    /*
    private void __initDevMouseHandler() {
        tacViewer.setOnMouseClicked(event -> {
            final MapTopographyRequestDto mapTopographyRequest = MapTopographyRequestDto.builder()
                    .mapName("$RECOMClient:worlds/Everon_CTI/RefCom_CTI_Campaign_Eden.ent")
                    .build();
            mapTopographyDataService.reloadMapTopographyData(mapTopographyRequest);
        });
    }
     */

    @EventListener(classes = ShutdownEvent.class)
    public void shutdown() {
        log.warn("Shutdown TacViewer ...");
        tacViewer.stop();
    }

}