package com.recom.client;

import com.recom.client.property.RendererProperties;
import com.recom.client.property.SpringApplicationProperties;
import javafx.application.Application;
import lombok.NonNull;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(scanBasePackages = {"com.recom"})
@EnableConfigurationProperties({
        SpringApplicationProperties.class,
        RendererProperties.class
})
public class RecomClientApplication {

    public static void main(@NonNull final String[] args) {
        Application.launch(JavaFxApplication.class, args);
    }

}
