package com.recom.commander.property.user;

import com.recom.commander.RecomCommanderApplication;
import com.recom.dynamicproperties.ObservableDynamicUserProperties;
import lombok.*;

import java.time.Duration;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DynamicHostProperties extends ObservableDynamicUserProperties<DynamicHostProperties> {

    @NonNull
    @Override
    public String getApplicationName() {
        return RecomCommanderApplication.APPLICATION_NAME;
    }

    @NonNull
    @Override
    public String getPropertyFileName() {
        return "host";
    }

    @Builder.Default
    private String protocol = "http";
    @Builder.Default
    private String hostname = "localhost";
    @Builder.Default
    private String port = "8080";
    @Builder.Default
    private Duration duration = Duration.ofSeconds(10);

    public String getHostBasePath() {
        return protocol + "://" + hostname + ":" + port;
    }

}
