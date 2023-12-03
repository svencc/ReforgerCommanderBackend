package com.recom.commander;

import com.recom.commander.property.RendererProperties;
import com.recom.commander.property.SpringApplicationProperties;
import javafx.application.Application;
import lombok.NonNull;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(scanBasePackages = {"com.recom"})
@EnableConfigurationProperties({
        SpringApplicationProperties.class,
        RendererProperties.class
})
public class RecomCommanderApplication {

    public static void main(@NonNull final String[] args) {
        Application.launch(JavaFxApplication.class, args);
    }

}
