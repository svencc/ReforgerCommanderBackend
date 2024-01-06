package com.recom.tacview.engine.entity.component;

import com.recom.tacview.engine.entity.NullEntity;
import com.recom.tacview.engine.entity.interfaces.IsComponent;
import com.recom.tacview.engine.entity.interfaces.IsEntity;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
public abstract class ComponentTemplate implements IsComponent {

    @Setter
    @NonNull
    private IsEntity entity = NullEntity.INSTANCE();

    @NonNull
    private final ComponentType componentType;


    @NonNull
    public Class<? extends ComponentTemplate> componentClass() {
        return componentType.getComponentClass();
    }

    @Override
    public ComponentType componentType() {
        return componentType;
    }

    @Setter
    protected int processingOrderOffset = 0;


    public final int getComponentProcessingOrder() {
        return componentType().getProcessingOrder() + processingOrderOffset;
    }

    @Override
    public boolean hasNullEntity() {
        return entity.equals(NullEntity.INSTANCE());
    }

    public void update(final long elapsedNanoTime) {

    }

}
