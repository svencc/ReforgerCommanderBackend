package com.recom.client.controller;

import com.recom.dto.ServerMessageDto;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HelloController {

    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
        ServerMessageDto serverMessageDto = new ServerMessageDto();
    }
}