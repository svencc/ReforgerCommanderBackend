package com.recom.commander;

import com.recom.commander.event.InitEvent;
import com.recom.commander.event.ShutdownEvent;
import com.recom.commander.event.StageReadyEvent;
import com.recom.commander.exception.GlobalExceptionHandler;
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
        // Start Spring Boot Application
        applicationContext = new SpringApplicationBuilder(RecomCommanderApplication.class).run();
        final GlobalExceptionHandler globalExceptionHandler = (GlobalExceptionHandler) applicationContext.getBean("globalExceptionHandler");
        Thread.setDefaultUncaughtExceptionHandler(globalExceptionHandler);

        // SPRING Context is ready; publish InitEvent
        log.info("* Spring Context initialized > Initializing Application ...");
        applicationContext.publishEvent(new InitEvent(this));
        log.info("+--- all application components initialized.");
    }

    @Override
    public void start(@NonNull final Stage stage) {
        applicationContext.publishEvent(new StageReadyEvent(stage));
    }

    @Override
    public void stop() {
        log.warn("Closing application context ...");
        applicationContext.publishEvent(new ShutdownEvent(this));
        applicationContext.close();
        log.warn("Exit ...");
        Platform.exit();
    }

}
