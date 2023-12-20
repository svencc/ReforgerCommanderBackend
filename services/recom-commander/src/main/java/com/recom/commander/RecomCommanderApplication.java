package com.recom.commander;

import javafx.application.Application;
import lombok.NonNull;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = {"com.recom"})
public class RecomCommanderApplication {

    public static final String APPLICATION_NAME = "RECOMCommander";

    public static void main(@NonNull final String[] args) {
        Application.launch(JavaFxApplication.class, args);
    }

}
