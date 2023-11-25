package com.recom.client;

import com.recom.client.event.StageReadyEvent;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.lang.Nullable;

@Slf4j
public class JavaFxApplication extends Application {

    @Nullable
    private ConfigurableApplicationContext applicationContext;

    @Override
    public void init() {
        applicationContext = new SpringApplicationBuilder(RecomClientApplication.class).run();
    }

    @Override
    public void start(@NonNull final Stage stage) {
        applicationContext.publishEvent(new StageReadyEvent(stage));
    }

    @Override
    public void stop() {
        log.warn("Closing application context...");
        applicationContext.close();
        Platform.exit();
    }

}
