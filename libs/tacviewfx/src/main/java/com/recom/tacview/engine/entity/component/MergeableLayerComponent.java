package com.recom.tacview.engine.entity.component;

import com.recom.tacview.engine.entity.Entity;
import com.recom.tacview.engine.renderables.HasMergeable;
import com.recom.tacview.engine.renderables.Mergeable;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

//public abstract class MergeableComponent extends ComponentBase implements Mergeable {
@RequiredArgsConstructor
public class MergeableLayerComponent extends ComponentBase implements HasMergeable {

    @NonNull
    private final Mergeable mergeable;


    @Override
    public @NonNull ComponentType componentType() {
        return null;
    }

    @Override
    public void update(
            @NonNull final Entity entity,
            final long elapsedNanoTime
    ) {
        // nothing to update
    }

    @Override
    public Mergeable getMergeable() {
        return mergeable;
    }
}
