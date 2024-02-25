package com.recom.commons.map.rasterizer.configuration;

import com.recom.commons.model.maprendererpipeline.MapLayerRendererConfiguration;
import lombok.NonNull;

public interface HasMapLayerRendererConfiguration {

    @NonNull
    MapLayerRendererConfiguration getMapLayerRendererConfiguration();

}
