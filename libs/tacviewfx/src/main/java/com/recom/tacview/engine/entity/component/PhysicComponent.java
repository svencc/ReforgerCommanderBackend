package com.recom.tacview.engine.entity.component;

import com.recom.tacview.engine.entity.Entity;
import lombok.NonNull;

public class PhysicComponent extends ComponentBase {

    @NonNull
    public ComponentType componentType() {
        return ComponentType.PHYSICS;
    }

    @Override
    public void update(
            @NonNull final Entity entity,
            final long elapsedNanoTime
    ) {
        // move the component -> using physics engine ...
    }

}
