package com.recom.tacview.engine.module;

import com.recom.tacview.engine.entitycomponentsystem.environment.Environment;
import com.recom.tacview.engine.renderer.RenderProvider;
import com.recom.tacview.property.RendererProperties;
import lombok.NonNull;

public class DefaultEnvironment extends Environment {

    public DefaultEnvironment(
            @NonNull final RendererProperties rendererProperties,
            @NonNull final RenderProvider renderProvider
    ) {
        super(rendererProperties, renderProvider);
    }
}
