package com.recom.tacview.engine.module;

import com.recom.tacview.engine.ecs.entity.Entity;
import com.recom.tacview.engine.ecs.environment.IsEnvironment;
import com.recom.tacview.engine.graphics.renderpipeline.IsRenderPipeline;
import com.recom.tacview.engine.graphics.renderpipeline.NullRenderPipeline;
import com.recom.tacview.engine.renderer.RenderProvider;
import com.recom.tacview.property.RendererProperties;
import com.recom.tacview.service.RendererExecutorProvider;
import lombok.NonNull;

import java.util.Collections;
import java.util.List;

public class NullEnvironment implements IsEnvironment {

    @NonNull
    public static final NullEnvironment INSTANCE = new NullEnvironment();

    private NullEnvironment() {
    }

    @Override
    public @NonNull List<Entity> getEntities() {
        return Collections.emptyList();
    }

    @Override
    public void registerNewEntity(@NonNull Entity entity) {

    }

    @Override
    public void registerNewEntities(@NonNull List<Entity> entitiesToAdd) {

    }

    @Override
    public void update(long elapsedNanoTime) {

    }

    @NonNull
    @Override
    public RendererProperties getRendererProperties() {
        return null; // @TODO da müssen echte rein ...
    }

    @NonNull
    @Override
    public RenderProvider getRenderProvider() {
        return null; // @TODO da müssen echte rein ...
    }

    @NonNull
    @Override
    public IsRenderPipeline getRenderPipeline() {
        return NullRenderPipeline.INSTANCE;
    }

    @NonNull
    public RendererExecutorProvider getRendererExecutorProvider() {
        return null; // @TODO da muss ein null rein? ... oder Optionals gegen die NullVarianten ...
    }

}
