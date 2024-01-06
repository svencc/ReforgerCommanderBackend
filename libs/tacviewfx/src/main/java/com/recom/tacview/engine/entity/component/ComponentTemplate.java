package com.recom.tacview.engine.entity.component;

import com.recom.tacview.engine.entity.NullEntity;
import com.recom.tacview.engine.entity.interfaces.IsComponent;
import com.recom.tacview.engine.entity.interfaces.IsEntity;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public abstract class ComponentTemplate implements IsComponent {

    @Setter
    @Getter
    @NonNull
    private IsEntity entity = NullEntity.INSTANCE();

    @Getter
    @NonNull
    private final Class<? extends ComponentTemplate> componentClass;

    @Override
    public abstract ComponentType componentType();

    @Getter
    @Setter
    protected int processingOrderOffset = 0;


    public final int getComponentProcessingOrder() {
        return componentType().getProcessingOrder() + processingOrderOffset;
    }

    @Override
    public boolean hasRealEntity() {
        return !entity.equals(NullEntity.INSTANCE());
    }

    public void update(final long elapsedNanoTime) {

    }

}
