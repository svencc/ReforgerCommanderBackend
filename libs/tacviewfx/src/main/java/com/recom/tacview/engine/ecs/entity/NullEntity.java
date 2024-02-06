package com.recom.tacview.engine.ecs.entity;

import com.recom.tacview.engine.ecs.component.ComponentType;
import com.recom.tacview.engine.ecs.component.IsComponent;
import lombok.NonNull;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class NullEntity extends Entity {

    @NonNull
    public static final NullEntity INSTANCE = new NullEntity();


    private NullEntity() {
    }

    @Override
    public void addComponent(@NonNull final IsComponent component) {
        // Do nothing
    }

    @Override
    public void addComponents(@NonNull final List<IsComponent> components) {
    }

    @Override
    public void reIndexComponents() {
        // Do nothing
    }

    @Override
    public void removeComponent(@NonNull final IsComponent component) {
        // Do nothing
    }

    @NonNull
    @Override
    public <T extends IsComponent> Optional<T> locateComponent(@NonNull final ComponentType componentType) {
        return Optional.empty();
    }

    @NonNull
    @Override
    public <T extends IsComponent> List<T> locateComponents(@NonNull final ComponentType componentType) {
        return Collections.emptyList();
    }

    @NonNull
    @Override
    public List<IsComponent> getComponents() {
        return Collections.emptyList();
    }

    @Override
    public void update(final long elapsedNanoTime) {
        // Do nothing
    }

}
