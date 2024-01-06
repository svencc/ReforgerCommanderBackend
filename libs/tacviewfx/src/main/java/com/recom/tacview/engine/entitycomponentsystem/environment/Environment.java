package com.recom.tacview.engine.entitycomponentsystem.environment;

import com.recom.tacview.engine.entitycomponentsystem.component.ComponentType;
import com.recom.tacview.engine.entitycomponentsystem.entity.Entity;
import com.recom.tacview.engine.graphics.renderpipeline.IsRenderPipeline;
import com.recom.tacview.engine.graphics.renderpipeline.RenderPipeline;
import com.recom.tacview.engine.renderer.RenderProvider;
import com.recom.tacview.property.RendererProperties;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public abstract class Environment implements IsEnvironment {

    @Getter
    private final RendererProperties rendererProperties;
    @Getter
    private final RenderProvider renderProvider;

    @NonNull
    private final List<Entity> entities = new ArrayList<>();
    @Getter
    @NonNull
    private final IsRenderPipeline renderPipeline = new RenderPipeline(this);


    @NonNull
    @Override
    public List<Entity> getEntities() {
        return Collections.unmodifiableList(entities);
    }

    @Override
    public void registerNewEntity(@NonNull final Entity entity) {
        final boolean hasRenderableComponents = entity.locateComponents(ComponentType.RenderableComponent).isEmpty();
        entity.setEnvironment(this);
        entities.add(entity);

        if (hasRenderableComponents) {
            renderPipeline.setDirty(true);
        }
    }

    @Override
    public void registerNewEntities(@NonNull final List<Entity> entitiesToAdd) {
        final boolean hasRenderableComponents = entitiesToAdd.stream()
                .map(entity -> entity.locateComponents(ComponentType.RenderableComponent).isEmpty())
                .reduce(false, (first, second) -> first || second);

        entities.addAll(entitiesToAdd);

        if (hasRenderableComponents) {
            renderPipeline.setDirty(true);
        }
    }

    public void update(final long elapsedNanoTime) {
        for (final Entity entity : entities) {
            entity.update(elapsedNanoTime);
        }
    }

}
