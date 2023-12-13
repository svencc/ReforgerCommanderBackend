package com.recom.commander.property.user;

import com.recom.dynamicproperties.ObservableDynamicUserProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HostProperties extends ObservableDynamicUserProperties<HostProperties> {

    @Override
    public String getApplicationName() {
        return "RECOMCommander";
    }

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
