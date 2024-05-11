package com.recom.commons.model.maprendererpipeline;

import com.recom.commons.map.rasterizer.configuration.MapLayerRasterizer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Builder
@AllArgsConstructor
public class MapRendererPipelineArtifacts {

    @Getter
    @NonNull
    private final Map<Class<? extends MapLayerRasterizer>, CreatedArtifact> artifacts = new ConcurrentHashMap<>();


    public void addArtifact(
            @NonNull final MapLayerRasterizer creator,
            @NonNull final Object artifact
    ) {
        final CreatedArtifact createdArtifact = CreatedArtifact.builder()
                .creator(creator)
                .data(artifact)
                .build();

        artifacts.put(creator.getClass(), createdArtifact);
    }

    public <T> Optional<CreatedArtifact> getArtifactFrom(@NonNull final Class<? extends MapLayerRasterizer> rendererName) {
        return Optional.ofNullable(artifacts.get(rendererName));
    }

}
