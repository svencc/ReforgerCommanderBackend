package com.recom.commander;

import com.recom.commander.event.InitializeComponentsEvent;
import com.recom.commander.event.InitializeStageEvent;
import com.recom.commander.event.ShutdownEvent;
import com.recom.commander.event.StageReadyEvent;
import com.recom.commander.exception.GlobalExceptionHandler;
import com.recom.commander.util.LoggerUtil;
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
        setGlobalExceptionHandler();

        // SPRING Context is ready; publish InitEvent
        log.info("* Spring Context initialized > Initializing Application ...");
        applicationContext.publishEvent(new InitializeComponentsEvent(this));
        log.info("+--- all application components initialized.");
    }

    @Override
    public void start(@NonNull final Stage stage) {
        // Set JavaFX Exception Handler
        setGlobalExceptionHandler();

        log.info("* Initializing Stage ...");
        applicationContext.publishEvent(new InitializeStageEvent(this, stage));
        log.info("+--- Stage initialized.");

        log.info("* Finalizing Stage ...");
        applicationContext.publishEvent(new StageReadyEvent(this, stage));
        log.info("+--- Stage finalized.");
    }

    private void setGlobalExceptionHandler() {
        final GlobalExceptionHandler globalExceptionHandler = (GlobalExceptionHandler) applicationContext.getBean("globalExceptionHandler");
        Thread.currentThread().setUncaughtExceptionHandler(globalExceptionHandler);
        Thread.setDefaultUncaughtExceptionHandler(globalExceptionHandler);
    }

    @Override
    public void stop() {
        System.out.println("\n\n" + LoggerUtil.generateCenteredString("STOP"));
        log.warn("Closing application context ...");
        applicationContext.publishEvent(new ShutdownEvent(this));
        applicationContext.close();
        log.warn("Exit ...");
        Platform.exit();
        System.exit(0);
    }

}
