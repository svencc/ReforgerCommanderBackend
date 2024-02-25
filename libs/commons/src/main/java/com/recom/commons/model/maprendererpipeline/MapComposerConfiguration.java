package com.recom.commons.model.maprendererpipeline;

import com.recom.commons.map.rasterizer.mapdesignscheme.MapDesignScheme;
import com.recom.commons.model.DEMDescriptor;
import lombok.Data;
import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;

@Data
public class MapComposerConfiguration {

    @NonNull
    private DEMDescriptor demDescriptor;

    @NonNull
    private MapDesignScheme mapDesignScheme;

    @NonNull
    private final Map<String, MapLayerRendererConfiguration> rendererConfiguration = new HashMap<>();

}
