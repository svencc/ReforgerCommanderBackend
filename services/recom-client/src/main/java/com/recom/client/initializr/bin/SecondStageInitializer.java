//package com.recom.client.initializr;
//
//import com.recom.client.event.StageReadyEvent;
//import javafx.scene.Scene;
//import javafx.scene.control.Label;
//import javafx.scene.control.TextField;
//import javafx.scene.layout.Pane;
//import javafx.scene.paint.Color;
//import javafx.scene.shape.Circle;
//import javafx.scene.text.Font;
//import javafx.stage.Stage;
//import lombok.NonNull;
//import org.springframework.context.ApplicationListener;
//import org.springframework.stereotype.Component;
//
//
//@Component
//public class SecondStageInitializer implements ApplicationListener<StageReadyEvent> {
//
//    @Override
//    public void onApplicationEvent(@NonNull final StageReadyEvent __) {
//        final Stage secondStage = new Stage();
//        populateSecondStage(secondStage);
////        secondStage.show();
//    }
//
//    @NonNull
//    private void populateSecondStage(@NonNull final Stage secondStage) {
//        final Pane root = new Pane();
//        final Scene scene = new Scene(root, 400, 400);
//
//        // Circle
//        final Circle circle = new Circle(200, 200, 100);
//        circle.setCenterX(100);
//        circle.setCenterY(100);
//        circle.setRadius(60);
//        circle.setFill(Color.RED);
//
//        circle.centerXProperty().bind(root.widthProperty().divide(2));
//        circle.centerYProperty().bind(root.heightProperty().divide(2));
//        circle.radiusProperty().bind(root.widthProperty().divide(4));
//
//
//        // TextField
//        final TextField textField = new TextField();
//        textField.setMaxWidth(300);
//
//        final Label label = new Label();
//        label.textProperty().bind(textField.textProperty());
//        label.setFont(new Font(20));
//        label.setLayoutX(100);
//        label.setLayoutY(100);
//
//        // Add TextField and Label to the Pane
//        root.getChildren().add(circle);
//        root.getChildren().add(textField);
//        root.getChildren().add(label);
//
//        secondStage.setTitle("Second Stage");
//        secondStage.setScene(scene);
//    }
//
//}
