package com.recom.client;

import javafx.application.Application;
import lombok.NonNull;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RecomClientApplication {

	public static void main(@NonNull final String[] args) {
		Application.launch(RecomClientFxApplication.class, args);
	}

}
