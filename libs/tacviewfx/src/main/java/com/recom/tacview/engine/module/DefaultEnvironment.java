package com.recom.tacview.engine.module;

import com.recom.tacview.engine.ecs.environment.Environment;
import com.recom.tacview.engine.renderer.RenderProvider;
import com.recom.tacview.property.EngineProperties;
import com.recom.tacview.service.RendererExecutorProvider;
import lombok.NonNull;

public class DefaultEnvironment extends Environment {

    public DefaultEnvironment(
            @NonNull final EngineProperties engineProperties,
            @NonNull final RenderProvider renderProvider,
            @NonNull final RendererExecutorProvider rendererExecutorProvider
    ) {
        super(engineProperties, renderProvider, rendererExecutorProvider);
    }

}
