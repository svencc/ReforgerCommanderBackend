package com.recom.tacview.engine.entity.component;

import com.recom.tacview.engine.entity.Entity;
import com.recom.tacview.engine.renderables.HasMergeable;
import com.recom.tacview.engine.renderables.Mergeable;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MergeableLayerComponent extends ComponentBase implements HasMergeable {

    @Getter
    @NonNull
    private final Mergeable mergeable;


    @Override
    public @NonNull ComponentType componentType() {
        return ComponentType.LAYER;
    }

    @Override
    public void update(
            @NonNull final Entity entity,
            final long elapsedNanoTime
    ) {
        // nothing to update
    }

}
