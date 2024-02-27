package com.recom.commons.map.rasterizer.configuration;

import com.recom.commons.model.maprendererpipeline.MapComposerWorkPackage;
import lombok.NonNull;

import java.io.IOException;

public interface MapLayerRasterizer extends HasMapLayerRasterizerConfiguration {

    @NonNull
    String getRasterizerName();

    void render(@NonNull final MapComposerWorkPackage workPackage) throws IOException;

}
