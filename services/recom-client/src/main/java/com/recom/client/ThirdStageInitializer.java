package com.recom.client;

import com.recom.client.event.StageReadyEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import lombok.NonNull;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;


@Component
public class ThirdStageInitializer implements ApplicationListener<StageReadyEvent> {

    @Override
    public void onApplicationEvent(@NonNull final StageReadyEvent __) {
        final Stage thirdStage = new Stage();
        populateThirdStage(thirdStage);
        thirdStage.show();
    }

    @NonNull
    private void populateThirdStage(@NonNull final Stage thirdStage) {
        final GridPane root = new GridPane();
        root.setAlignment(javafx.geometry.Pos.CENTER);
        root.setVgap(10);
        root.setHgap(10);

        // Elements
        final Text login = new Text("Login");
        final TextField textField = new TextField("Username");
        final PasswordField passwordField = new PasswordField();
        final Button button = new Button("Login");
        final Button button2 = new Button("Login");

        root.add(login, 0, 0);
        root.add(textField, 0, 1);
        root.add(passwordField, 0, 2);
        root.add(button, 0, 3);
        root.add(button2, 0, 4);

        // Eventhandling
        final EventHandler<MouseEvent> eventHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(final MouseEvent e) {
                final String username = textField.getText();
                final String password = passwordField.getText();

                if (username.equals("Peter") && password.equals("123!")) {
                    login.setText("Login successful");
                    login.setFill(Color.GREEN);
                } else {
                    login.setText("Login failed");
                    login.setFill(Color.RED);
                }
            }
        };
        button.addEventHandler(MouseEvent.MOUSE_CLICKED, eventHandler);
        button2.addEventHandler(MouseEvent.MOUSE_ENTERED, eventHandler);



        final Scene scene = new Scene(root, 400, 400);
        thirdStage.setTitle("Third Stage");
        thirdStage.setScene(scene);
    }

}
