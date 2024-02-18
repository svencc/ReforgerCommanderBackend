package com.recom.commander.initializr;

import com.recom.commander.event.InitializeStageEvent;
import com.recom.commander.event.ShutdownEvent;
import com.recom.commander.exception.GlobalExceptionHandler;
import com.recom.commander.property.SpringApplicationProperties;
import com.recom.commander.property.user.DynamicEngineProperties;
import com.recom.commander.util.LoggerUtil;
import com.recom.observer.Notification;
import com.recom.observer.ReactiveObserver;
import com.recom.observer.Subjective;
import com.recom.tacview.engine.TacViewer;
import com.recom.tacview.engine.graphics.ScreenComposer;
import com.recom.tacview.engine.input.GenericFXInputEventListener;
import com.recom.tacview.engine.input.InputManager;
import com.recom.tacview.engine.module.EngineModule;
import com.recom.tacview.property.IsEngineProperties;
import com.recom.tacview.service.profiler.ProfilerProvider;
import com.recom.tacview.strategy.ProfileFPSStrategy;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;


@Slf4j
@Component
@RequiredArgsConstructor
public class TacViewStageInitializer {

    @NonNull
    private final GlobalExceptionHandler globalExceptionHandler;
    @NonNull
    private final SpringApplicationProperties springApplicationProperties;
    @NonNull
    private final IsEngineProperties engineProperties;
    @NonNull
    private final ProfilerProvider profilerProvider;
    @NonNull
    private final ScreenComposer screenComposer;
    @NonNull
    private final EngineModule engineModule;
    @NonNull
    private final GenericFXInputEventListener genericFXInputEventListener;
    @NonNull
    private final InputManager inputManager;
    @NonNull
    private final DynamicEngineProperties dynamicEngineProperties;
    @NonNull
    private Optional<TacViewer> maybeTacViewer = Optional.empty();
    private int titleBarHeight = 0;


    @EventListener(classes = InitializeStageEvent.class)
    public void onApplicationEvent(@NonNull final InitializeStageEvent event) {
        try {
            event.logStageInitializationWithMessage(log, TacViewStageInitializer.class, "Starting TacView");
            final Stage stage = event.getStage();

            populateTacViewStage(stage);
            stage.show();

            addTitleBarToStageHeight(stage);

            addWindowResizeListener(stage);

            stage.setOnCloseRequest(onCloseEvent -> {
                System.out.println("\n\n" + LoggerUtil.generateCenteredString("CLOSED"));
                Platform.exit();
            });
        } catch (final Throwable t) {
            globalExceptionHandler.uncaughtException(Thread.currentThread(), t);
        }
    }

    private void addWindowResizeListener(@NonNull final Stage stage) {
        final ChangeListener<Number> windowSizeListener = provideWindowSizeListener(stage);
        stage.widthProperty().addListener(windowSizeListener);
        stage.heightProperty().addListener(windowSizeListener);
    }

    private void addTitleBarToStageHeight(@NonNull final Stage stage) {
        // Calculate title bar height
        final Scene scene = stage.getScene();
        int sceneHeight = (int) scene.getHeight();
        titleBarHeight = (int) stage.getHeight() - sceneHeight;

        // Correct stage height:
        // Set stage height to scene height + title bar height (title bar height is not included in scene height and in the engine properties window height)
        stage.setHeight(sceneHeight + titleBarHeight);
    }

    private void populateTacViewStage(@NonNull final Stage stage) {
        final BorderPane root = new BorderPane();
        root.setBackground(new Background(new BackgroundFill(Color.YELLOWGREEN, null, null)));

        final TacViewer tacViewer = new TacViewer(
                engineProperties,
                profilerProvider,
                screenComposer,
                engineModule,
                genericFXInputEventListener,
                inputManager,
                globalExceptionHandler

        );

        stage.widthProperty().addListener((obs, oldVal, newVal) -> {
            tacViewer.setWidth(newVal.doubleValue());
        });
        stage.heightProperty().addListener((obs, oldVal, newVal) -> {
            tacViewer.setHeight(newVal.doubleValue() - titleBarHeight);
        });
        provideDebouncedEnginePropertyListener(stage);

        maybeTacViewer = Optional.of(tacViewer);
        root.setCenter(maybeTacViewer.get());

        final Scene scene = new Scene(root, engineProperties.getScaledWindowWidth(), engineProperties.getScaledWindowHeight());
        stage.setTitle(springApplicationProperties.getName());

        configureStage(stage, tacViewer);

        tacViewer.start();

        stage.setResizable(true);
        stage.setScene(scene);
    }

    private void configureStage(
            @NonNull final Stage stage,
            @NonNull final TacViewer tacViewer
    ) {
        // Initialize stage size
        stage.setWidth(engineProperties.getScaledWindowWidth());
        stage.setHeight(engineProperties.getScaledWindowHeight());

//        final Window window = stage.getOwner();
//        stage.initStyle(StageStyle.UNDECORATED); // BORDERLESS -> NEED to make movable on your own!

        // FPS Profile Strategy (displayed in window title)
        tacViewer.setMaybeProfileFPSStrategy(Optional.of(provideFpsStrategy(stage.titleProperty())));
    }

    private void provideDebouncedEnginePropertyListener(@NonNull final Stage stage) {
        final ReactiveObserver<DynamicEngineProperties> enginePropertiesReactiveObserver = ReactiveObserver.reactWith((
                        @NonNull final Subjective<DynamicEngineProperties> subject,
                        @NonNull final Notification<DynamicEngineProperties> notification
                ) -> {
                    stage.setWidth(dynamicEngineProperties.getRendererWidth() * dynamicEngineProperties.getRendererScale());
                    stage.setHeight(titleBarHeight + (dynamicEngineProperties.getRendererHeight() * dynamicEngineProperties.getRendererScale()));
                    dynamicEngineProperties.invalidAllCaches();
                },
                Duration.ofMillis(500)
        );
        enginePropertiesReactiveObserver.observe(dynamicEngineProperties.getSubject());
    }

    @NonNull
    private ChangeListener<Number> provideWindowSizeListener(@NonNull final Stage stage) {
        return new ChangeListener<Number>() {
            @Override
            public void changed(
                    @NonNull final ObservableValue<? extends Number> observableValue,
                    @NonNull final Number oldSize,
                    @NonNull final Number newSize
            ) {
                dynamicEngineProperties.setRendererWidth((int) stage.getWidth() / dynamicEngineProperties.getRendererScale());
                dynamicEngineProperties.setRendererHeight((int) (stage.getHeight() - titleBarHeight) / dynamicEngineProperties.getRendererScale());
                dynamicEngineProperties.persist();
            }
        };
    }

    @NonNull
    private ProfileFPSStrategy provideFpsStrategy(@NonNull final StringProperty titleProperty) {
        return new ProfileFPSStrategy((@NonNull final String profiled) -> {
            Platform.runLater(() -> titleProperty.setValue(profiled));
        });
    }

    @EventListener(classes = ShutdownEvent.class)
    public void shutdown() {
        log.warn("Shutdown TacViewer ...");
        maybeTacViewer.ifPresent(TacViewer::stop);
    }

}