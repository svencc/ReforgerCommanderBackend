package com.recom.client.initializr;

import com.recom.client.event.StageReadyEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lombok.NonNull;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;


@Component
public class CanvasStageInitializer implements ApplicationListener<StageReadyEvent> {

    @Override
    public void onApplicationEvent(@NonNull final StageReadyEvent __) {
        final Stage canvasStage = new Stage();
        populateCanvasStage(canvasStage);
//        canvasStage.show();
    }


    @NonNull
    private void populateCanvasStage(@NonNull final Stage canvasStage) {
        final BorderPane root = new BorderPane();
        root.setBackground(new Background(new BackgroundFill(Color.rgb(230, 185, 120), CornerRadii.EMPTY, Insets.EMPTY)));
        final Canvas canvas = new Canvas(300, 300);
        root.setCenter(canvas);

        drawSquares(canvas.getGraphicsContext2D());


        final Scene scene = new Scene(root, 400, 400, Color.rgb(230, 185, 120));
        canvasStage.setTitle("Canvas Stage");
        canvasStage.setScene(scene);
    }

    private void drawSquares(@NonNull final GraphicsContext gc) {
        gc.setFill(Color.rgb(255, 120, 35));
        gc.fillRect(25, 30, 250, 250);

        gc.setFill(Color.rgb(255, 135, 5));
        gc.fillRect(60, 85, 180, 180);

        gc.setFill(Color.rgb(5, 180, 180));
        gc.fillRect(85, 125, 130, 130);
    }

}
