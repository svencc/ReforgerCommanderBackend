package com.recom.tacview.engine.entity.component;

import com.recom.tacview.engine.entity.Entity;
import lombok.NonNull;

public abstract class MapComponent extends RenderableComponent {

    @NonNull
    public ComponentType componentType() {
        return ComponentType.MAP_LAYER;
    }


    @Override
    public void update(
            @NonNull final Entity entity,
            final long elapsedNanoTime
    ) {
        // render the component
        // needs to talk with physics component (position, velocity)
    }

}
