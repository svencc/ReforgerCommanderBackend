package com.recom.commons.model.maprendererpipeline;

import com.recom.commons.map.rasterizer.configuration.MapLayerRenderer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Builder
@AllArgsConstructor
public class MapRendererPipelineArtifacts {

    @NonNull
    private final Map<MapLayerRenderer, Object> artifacts = new HashMap<>();

    public void addArtifact(
            @NonNull final MapLayerRenderer renderer,
            @NonNull final Object artifact
    ) {
        artifacts.put(renderer, artifact);
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> getArtifact(@NonNull final MapLayerRenderer renderer) {
        return Optional.ofNullable((T) artifacts.get(renderer));
    }

}
