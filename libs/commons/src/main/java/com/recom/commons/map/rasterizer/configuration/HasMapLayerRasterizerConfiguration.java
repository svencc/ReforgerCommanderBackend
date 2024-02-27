package com.recom.commons.map.rasterizer.configuration;

import com.recom.commons.model.maprendererpipeline.MapLayerRendererConfiguration;
import lombok.NonNull;

public interface HasMapLayerRasterizerConfiguration {

    @NonNull MapLayerRendererConfiguration getMapLayerRendererConfiguration();

}
