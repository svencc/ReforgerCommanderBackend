package com.recom.commons.rasterizer.meta;

import com.recom.commons.model.MapRendererPipelineArtefacts;
import lombok.NonNull;

import java.io.IOException;

public interface MapLayerPipelineRenderer {

    boolean isVisible();

    @NonNull
    LayerOrder getLayerOrder();

    @NonNull
    MapRendererPipelineArtefacts render(@NonNull final MapRendererPipelineArtefacts pipelineArtefacts) throws IOException;

}
