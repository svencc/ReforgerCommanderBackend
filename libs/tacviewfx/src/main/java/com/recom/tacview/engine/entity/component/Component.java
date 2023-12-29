package com.recom.tacview.engine.entity.component;

import com.recom.tacview.engine.entity.Entity;
import lombok.NonNull;

public abstract class Component {

    public abstract void update(
            @NonNull final Entity entity,
            final double elapsedNanoTime
    );

}
