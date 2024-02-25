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
    private final Map<Class<? extends MapLayerRenderer>, CreatedArtifact> artifacts = new HashMap<>();


    public void addArtifact(
            @NonNull final MapLayerRenderer creator,
            @NonNull final Object artifact
    ) {
        final CreatedArtifact createdArtifact = CreatedArtifact.builder()
                .creator(creator)
                .data(artifact)
                .build();

        artifacts.put(creator.getClass(), createdArtifact);
    }

    public <T> Optional<CreatedArtifact> getArtifactFrom(@NonNull final Class<? extends MapLayerRenderer> rendererName) {
        return Optional.ofNullable(artifacts.get(rendererName));
    }

}
