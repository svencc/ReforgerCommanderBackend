package com.recom.tacview.engine.module;

import com.recom.tacview.engine.entitycomponentsystem.environment.Environment;
import com.recom.tacview.engine.renderer.RenderProvider;
import com.recom.tacview.property.RendererProperties;
import com.recom.tacview.service.RendererExecutorProvider;
import lombok.NonNull;

public class DefaultEnvironment extends Environment {

    public DefaultEnvironment(
            @NonNull final RendererProperties rendererProperties,
            @NonNull final RenderProvider renderProvider,
            @NonNull final RendererExecutorProvider rendererExecutorProvider
    ) {
        super(rendererProperties, renderProvider, rendererExecutorProvider);
    }

}
