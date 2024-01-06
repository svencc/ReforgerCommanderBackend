package com.recom.tacview.engine.entitycomponentsystem.environment;

import com.recom.tacview.engine.Updatable;
import com.recom.tacview.engine.renderer.RenderProvider;
import com.recom.tacview.property.RendererProperties;
import lombok.NonNull;

public interface IsEnvironment extends Updatable, HasManagableEntities {

    void update(final long elapsedNanoTime);

    @NonNull
    RendererProperties getRendererProperties();

    @NonNull
    RenderProvider getRenderProvider();

}
