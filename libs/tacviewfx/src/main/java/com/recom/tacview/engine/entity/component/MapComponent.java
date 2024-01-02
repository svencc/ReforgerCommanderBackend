package com.recom.tacview.engine.entity.component;

import com.recom.tacview.engine.entity.Entity;
import lombok.NonNull;

public abstract class MapComponent extends RenderableComponent {

    @NonNull
    public ComponentType componentType() {
        return ComponentType.RENDER_BACKGROUND;
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
