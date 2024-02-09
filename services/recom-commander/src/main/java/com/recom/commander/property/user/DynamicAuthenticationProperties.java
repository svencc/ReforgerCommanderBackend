package com.recom.commander.property.user;

import com.recom.commander.RecomCommanderApplication;
import com.recom.dynamicproperties.ObservableDynamicUserProperties;
import lombok.*;

import java.time.Duration;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DynamicAuthenticationProperties extends ObservableDynamicUserProperties<DynamicAuthenticationProperties> {

    @NonNull
    @Override
    public String getApplicationName() {
        return RecomCommanderApplication.APPLICATION_NAME;
    }

    @NonNull
    @Override
    public String getPropertyFileName() {
        return "authentication";
    }

    @Builder.Default
    private String accountUUID = "<accountUUID>";
    @Builder.Default
    private String accessKey = "<accessKey>";
    @Builder.Default
    private Duration reAuthenticateInAdvance = Duration.ofMinutes(1);

}
