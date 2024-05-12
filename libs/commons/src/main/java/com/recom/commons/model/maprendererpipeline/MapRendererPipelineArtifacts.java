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


    public <T> void addArtifact(
            @NonNull final MapLayerRasterizer<T> creator,
            @NonNull final T artifactData
    ) {
        final CreatedArtifact<?> createdArtifact = CreatedArtifact.builder()
                .creator(creator)
                .data(artifactData)
                .build();

        artifacts.put(creator.getClass(), createdArtifact);
    }

    @NonNull
    @SuppressWarnings("unchecked")
    public <T> Optional<CreatedArtifact<T>> getArtifactFrom(@NonNull final Class<? extends MapLayerRasterizer<T>> rendererClass) {
        final CreatedArtifact<T> artifact = artifacts.get(rendererClass);

        return Optional.ofNullable(artifact);
    }

}
