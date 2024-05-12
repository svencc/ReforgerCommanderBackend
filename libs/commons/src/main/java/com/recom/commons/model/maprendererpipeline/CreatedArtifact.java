package com.recom.commons.model.maprendererpipeline;

import com.recom.commons.map.rasterizer.configuration.MapLayerRasterizer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Builder
@AllArgsConstructor
public class CreatedArtifact<T> {

    @Getter
    @NonNull
    private final MapLayerRasterizer creator;
    @NonNull
    private final Object data;

    
    @SuppressWarnings("unchecked")
    public T getData() {
        return (T) data;
    }

}
