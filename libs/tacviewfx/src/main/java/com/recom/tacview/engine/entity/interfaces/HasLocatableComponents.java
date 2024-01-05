package com.recom.tacview.engine.entity.interfaces;

import com.recom.tacview.engine.entity.component.ComponentTemplate;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;

public interface HasLocatableComponents extends HasComponents {

    void addComponent(@NonNull final IsComponent component);

    void addComponents(@NonNull final List<IsComponent> components);

    void removeComponent(@NonNull final IsComponent component);

    @NonNull
    <T extends IsComponent> Optional<T> locateComponent(@NonNull final Class<T> componentClass);

    @NonNull
    <T extends IsComponent> List<T> locateComponents(@NonNull final Class<T> componentClass);

    void reIndexComponents();

}
