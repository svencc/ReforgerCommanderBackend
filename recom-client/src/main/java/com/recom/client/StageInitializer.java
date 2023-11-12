package com.recom.client;

import com.recom.client.event.StageReadyEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import lombok.NonNull;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class StageInitializer implements ApplicationListener<StageReadyEvent> {

    @Override
    public void onApplicationEvent(@NonNull final StageReadyEvent event) {
        final Stage stage = event.getStage();
        populateStage(stage);
        stage.show();
    }

    @NonNull
    private void populateStage(@NonNull final Stage stage) {
        try {
            final BorderPane root = new BorderPane();
            final FXMLLoader fxmlLoader = new FXMLLoader(StageInitializer.class.getResource("/view/hello-view.fxml"));
            final Scene scene = new Scene(fxmlLoader.load(), 320, 240);

            stage.setTitle("Hello!");
            stage.setScene(scene);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

//    @Override
//    public boolean supportsAsyncExecution() {
//        return ApplicationListener.super.supportsAsyncExecution();
//    }


}
