package com.recom.commons.model.maprendererpipeline;

import com.recom.commons.map.rasterizer.configuration.MapLayerRenderer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Builder
@AllArgsConstructor
public class CreatedArtifact {

    @Getter
    @NonNull
    private final MapLayerRenderer creator;
    @NonNull
    private final Object data;

    
    @SuppressWarnings("unchecked")
    public <T> T getData() {
        return (T) data;
    }

}
