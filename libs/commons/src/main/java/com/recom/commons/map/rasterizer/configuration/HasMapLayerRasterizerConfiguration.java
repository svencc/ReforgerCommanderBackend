package com.recom.commons.map.rasterizer.configuration;

import com.recom.commons.model.maprendererpipeline.MapLayerRasterizerConfiguration;
import lombok.NonNull;

public interface HasMapLayerRasterizerConfiguration {

    @NonNull MapLayerRasterizerConfiguration getMapLayerRasterizerConfiguration();

}
