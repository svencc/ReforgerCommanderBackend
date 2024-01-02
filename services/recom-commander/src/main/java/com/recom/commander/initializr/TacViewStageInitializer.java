package com.recom.commander.initializr;

import com.recom.commander.event.InitializeStageEvent;
import com.recom.commander.event.ShutdownEvent;
import com.recom.commander.property.SpringApplicationProperties;
import com.recom.tacview.engine.TacViewer;
import com.recom.tacview.engine.graphics.ScreenComposer;
import com.recom.tacview.engine.module.EngineModule;
import com.recom.tacview.property.RendererProperties;
import com.recom.tacview.property.TickProperties;
import com.recom.tacview.service.profiler.ProfilerProvider;
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
    private final TickProperties tickProperties;
    @NonNull
    private final RendererProperties rendererProperties;
    @NonNull
    private final ProfilerProvider profilerProvider;
    @NonNull
    private final ScreenComposer screenComposer;
    @NonNull
    private final EngineModule engineModule;

    @Nullable
    private TacViewer tacViewer = null;


    @EventListener(classes = InitializeStageEvent.class)
    public void onApplicationEvent(@NonNull final InitializeStageEvent event) {
        event.logStageInitializationWithMessage(log, TacViewStageInitializer.class, "Starting TacView");
        final Stage tacViewStage = event.getStage();
        populateTacViewStage(tacViewStage);
        tacViewStage.show();
    }

    private void populateTacViewStage(@NonNull final Stage stage) {
        final BorderPane root = new BorderPane();
        root.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));

        tacViewer = new TacViewer(
                rendererProperties,
                tickProperties,
                profilerProvider,
                screenComposer,
                engineModule
        );
        root.setCenter(tacViewer);

        final Scene scene = new Scene(root, rendererProperties.getWidth(), rendererProperties.getHeight());
        stage.setTitle(springApplicationProperties.getName());

//        final Window window = stage.getOwner();
//        stage.initStyle(StageStyle.UNDECORATED); // BORDERLESS -> NEED to make movable on your own!


        final StringProperty titleProperty = stage.titleProperty();

        final ProfileFPSStrategy profileFPSStrategy = new ProfileFPSStrategy((@NonNull final String profiled) -> {
            Platform.runLater(() -> titleProperty.setValue(profiled));
        });
        tacViewer.setProfileFPSStrategy(profileFPSStrategy);
        tacViewer.start();

        //__exampleHandler("test");

        stage.setResizable(false);
        stage.setScene(scene);
    }

    private void __exampleHandler(@NonNull final StringProperty stringProperty) {
        tacViewer.setOnMouseClicked(event -> {
            stringProperty.setValue(String.valueOf(Math.random()));
        });
    }

    @EventListener(classes = ShutdownEvent.class)
    public void shutdown() {
        log.warn("Shutdown TacViewer ...");
        tacViewer.stop();
    }

}