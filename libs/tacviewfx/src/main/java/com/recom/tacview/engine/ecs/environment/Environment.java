package com.recom.tacview.engine.ecs.environment;

import com.recom.tacview.engine.ecs.component.ComponentType;
import com.recom.tacview.engine.ecs.entity.Entity;
import com.recom.tacview.engine.graphics.renderpipeline.IsRenderPipeline;
import com.recom.tacview.engine.graphics.renderpipeline.RenderPipeline;
import com.recom.tacview.engine.renderer.RenderProvider;
import com.recom.tacview.property.IsEngineProperties;
import com.recom.tacview.service.RendererExecutorProvider;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public abstract class Environment {

    @Getter
    @NonNull
    private final IsEngineProperties engineProperties;
    @Getter
    @NonNull
    private final RenderProvider renderProvider;
    @Getter
    @NonNull
    private final RendererExecutorProvider rendererExecutorProvider;
    @Getter
    @NonNull
    private final IsRenderPipeline renderPipeline = new RenderPipeline(this);
    @NonNull
    private final List<Entity> entities = new ArrayList<>();


    @NonNull
    public List<Entity> getEntities() {
        return Collections.unmodifiableList(entities);
    }

    public void registerNewEntity(@NonNull final Entity entity) {
        entity.setEnvironment(this);
        entities.add(entity);

        final boolean hasRenderableComponents = !entity.locateComponents(ComponentType.RenderableComponent).isEmpty();
        if (hasRenderableComponents) {
            renderPipeline.updateLayers();
            renderPipeline.setDirty(true);
        }
    }

    public void registerNewEntities(@NonNull final List<Entity> entitiesToAdd) {
        final boolean hasRenderableComponents = entitiesToAdd.stream()
                .map(entity -> !entity.locateComponents(ComponentType.RenderableComponent).isEmpty())
                .reduce(false, (first, second) -> first || second);

        entities.addAll(entitiesToAdd);

        if (hasRenderableComponents) {
            renderPipeline.updateLayers();
            renderPipeline.setDirty(true);
        }
    }

    public void update(final long elapsedNanoTime) {
        for (final Entity entity : entities) {
            entity.update(elapsedNanoTime);
        }
    }

}
