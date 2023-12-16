package com.recom.commander;

import com.recom.commander.property.RendererProperties;
import com.recom.commander.property.RestClientProperties;
import com.recom.commander.property.SpringApplicationProperties;
import com.recom.commander.property.gateway.MapTopographyGatewayProperties;
import com.recom.commander.property.gateway.RECOMHostProperties;
import javafx.application.Application;
import lombok.NonNull;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(scanBasePackages = {"com.recom"})
@EnableConfigurationProperties({
        SpringApplicationProperties.class,
        RendererProperties.class,
        RestClientProperties.class,
        RECOMHostProperties.class,
        MapTopographyGatewayProperties.class
})
public class RecomCommanderApplication {

    public static void main(@NonNull final String[] args) {
        Application.launch(JavaFxApplication.class, args);
    }

}
