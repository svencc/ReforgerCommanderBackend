package com.recom.tacview.engine.entity;

import com.recom.tacview.engine.entity.component.ComponentTemplate;
import com.recom.tacview.engine.entity.interfaces.IsComponent;
import lombok.NonNull;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class NullEntity extends Entity {

    public static NullEntity INSTANCE() {
        return new NullEntity();
    }

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
    public <T extends IsComponent> Optional<T> locateComponent(@NonNull final Class<T> componentClass) {
        return Optional.empty();
    }

    @NonNull
    @Override
    public <T extends IsComponent> List<T> locateComponents(@NonNull final Class<T> componentClass) {
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
