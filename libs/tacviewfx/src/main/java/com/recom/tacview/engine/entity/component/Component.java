package com.recom.tacview.engine.entity.component;

import com.recom.tacview.engine.entity.Entity;
import lombok.NonNull;

public abstract class Component {

    @NonNull
    public abstract ComponentType componentType();

    public int getSortOrder() {
        return componentType().sortOrder();
    }

    public abstract void update(
            @NonNull final Entity entity,
            final long elapsedNanoTime
    );

}
