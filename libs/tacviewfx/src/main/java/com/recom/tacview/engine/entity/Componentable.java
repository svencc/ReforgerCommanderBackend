package com.recom.tacview.engine.entity;

import com.recom.tacview.engine.entity.component.ComponentBase;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;

public interface Componentable {

    @NonNull List<ComponentBase> getComponents();

    void addComponent(@NonNull final ComponentBase component);

    void addComponents(@NonNull final List<ComponentBase> components);

    void removeComponent(@NonNull final ComponentBase component);

    @NonNull <T extends ComponentBase> Optional<T> locateComponent(@NonNull final Class<T> componentClass);

    @NonNull <T extends ComponentBase> List<T> locateComponents(@NonNull final Class<T> componentClass);

}
