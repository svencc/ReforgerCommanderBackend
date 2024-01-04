package com.recom.tacview.engine.entity.component;

import com.recom.tacview.engine.entity.Entity;
import com.recom.tacview.engine.renderables.HasMergeable;
import com.recom.tacview.engine.renderables.Mergeable;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class MergeableLayerComponent extends ComponentBase implements HasMergeable {

    @Getter
    @NonNull
    private final Mergeable mergeable;

    @Setter
    private ComponentType componentType = ComponentType.BASE_LAYER;


    @Override
    public @NonNull ComponentType componentType() {
        return componentType;
    }

    @Override
    public void update(
            @NonNull final Entity entity,
            final long elapsedNanoTime
    ) {
        // nothing to update
    }

}
