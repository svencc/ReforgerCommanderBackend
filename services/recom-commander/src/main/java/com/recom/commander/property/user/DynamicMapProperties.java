package com.recom.commander.property.user;

import com.recom.commander.RecomCommanderApplication;
import com.recom.dynamicproperties.ObservableDynamicUserProperties;
import lombok.*;

import java.time.Duration;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DynamicMapProperties extends ObservableDynamicUserProperties<DynamicMapProperties> {

    @NonNull
    @Override
    public String getApplicationName() {
        return RecomCommanderApplication.APPLICATION_NAME;
    }

    @NonNull
    @Override
    public String getPropertyFileName() {
        return "map";
    }

    @Builder.Default
    private Integer scaleFactor = 1;

    @Builder.Default
    private Boolean showLayerX = false;

}
