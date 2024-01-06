package com.recom.tacview.engine.entitycomponentsystem.entity;

import com.recom.tacview.engine.entitycomponentsystem.component.ComponentType;
import com.recom.tacview.engine.entitycomponentsystem.component.IsComponent;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;

public interface HasLocatableComponents extends HasComponents {

    void addComponent(@NonNull final IsComponent component);

    void addComponents(@NonNull final List<IsComponent> components);

    void removeComponent(@NonNull final IsComponent component);

    @NonNull
    <T extends IsComponent> Optional<T> locateComponent(@NonNull final ComponentType componentType);

    @NonNull
    <T extends IsComponent> List<T> locateComponents(@NonNull final ComponentType componentType);

    void reIndexComponents();

}
