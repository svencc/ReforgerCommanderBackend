package com.recom.tacview.engine.ecs.component;

import com.recom.tacview.engine.ecs.entity.IsEntity;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

import java.util.Optional;

@Getter
@RequiredArgsConstructor
public abstract class ComponentTemplate implements IsComponent {

    @NonNull
    private Optional<IsEntity> maybeEntity = Optional.empty();
    @NonNull
    private final ComponentType componentType;


    public void setEntity(@Nullable final IsEntity entity) {
        this.maybeEntity = Optional.ofNullable(entity);
    }

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

    public void update(final long elapsedNanoTime) {

    }

}
