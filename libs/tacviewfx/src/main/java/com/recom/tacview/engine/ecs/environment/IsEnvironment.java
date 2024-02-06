package com.recom.tacview.engine.ecs.environment;

import com.recom.tacview.engine.IsUpdatable;
import com.recom.tacview.engine.graphics.renderpipeline.IsRenderPipeline;
import com.recom.tacview.engine.renderer.RenderProvider;
import com.recom.tacview.property.RendererProperties;
import com.recom.tacview.service.RendererExecutorProvider;
import lombok.NonNull;

public interface IsEnvironment extends IsUpdatable, HasManagableEntities {

    void update(final long elapsedNanoTime);

    @NonNull
    RendererProperties getRendererProperties();

    @NonNull
    RenderProvider getRenderProvider();

    @NonNull
    IsRenderPipeline getRenderPipeline();

    @NonNull
    RendererExecutorProvider getRendererExecutorProvider();

}