package com.recom.client.initializr;

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
public class MainStageInitializer implements ApplicationListener<StageReadyEvent> {

    @Override
    public void onApplicationEvent(@NonNull final StageReadyEvent mainStage) {
        final Stage stage = mainStage.getStage();
        populateMainStage(stage);
//        stage.show();
    }

    @NonNull
    private void populateMainStage(@NonNull final Stage mainStage) {
        try {
            final BorderPane root = new BorderPane();
            final FXMLLoader fxmlLoader = new FXMLLoader(MainStageInitializer.class.getResource("/view/hello-view.fxml"));
            final Scene scene = new Scene(fxmlLoader.load(), 320, 240);

            mainStage.setTitle("Hello!");
            mainStage.setScene(scene);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
