package com.recom.tacview.engine.ecs.entity;

import com.recom.tacview.engine.IsUpdatable;
import com.recom.tacview.engine.ecs.component.ComponentType;
import com.recom.tacview.engine.ecs.component.IsComponent;
import com.recom.tacview.engine.ecs.environment.Environment;
import lombok.Getter;
import lombok.NonNull;

import java.util.*;
import java.util.stream.Collectors;

public class Entity implements IsEntity {

    @NonNull
    private final List<IsComponent> components = new ArrayList<>();
    @Getter
    @NonNull
    private Optional<Environment> maybeEnvironment = Optional.empty();
    @NonNull
    private Map<? extends Class<? extends IsComponent>, List<IsComponent>> componentMap = new HashMap<>();


    public void setEnvironment(@NonNull final Environment maybeEnvironment) {
        this.maybeEnvironment = Optional.of(maybeEnvironment);
    }

    @Override
    public void addComponent(@NonNull final IsComponent component) {
        components.add(component);
        component.setEntity(this);
        components.sort(Comparator.comparing(IsComponent::getComponentProcessingOrder));
        reIndexComponents();
    }

    @Override
    public void addComponents(@NonNull final List<IsComponent> components) {
        this.components.addAll(components.stream().filter(Objects::nonNull).toList());
        this.components.sort(Comparator.comparing(IsComponent::getComponentProcessingOrder));
        reIndexComponents();
    }

    @Override
    public void reIndexComponents() {
        componentMap = components.stream()
                .sorted(Comparator.comparing(IsComponent::getComponentProcessingOrder))
                .collect(Collectors.groupingBy(IsComponent::componentClass));
    }

    @Override
    public void removeComponent(@NonNull final IsComponent component) {
        components.remove(component);
        component.setEntity(NullEntity.INSTANCE);
        reIndexComponents();
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends IsComponent> Optional<T> locateComponent(@NonNull final ComponentType componentType) {
        if (componentMap.containsKey(componentType.getComponentClass())) {
            return Optional.of((T) componentMap.get(componentType.getComponentClass()).getFirst());
        } else {
            return Optional.empty();
        }
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends IsComponent> List<T> locateComponents(@NonNull final ComponentType componentType) {
        if (componentMap.containsKey(componentType.getComponentClass())) {
            return Collections.unmodifiableList((List<? extends T>) componentMap.get(componentType.getComponentClass()));
        } else {
            return Collections.emptyList();
        }
    }

    @NonNull
    @Override
    public List<IsComponent> getComponents() {
        return Collections.unmodifiableList(components);
    }

    @Override
    public void update(final long elapsedNanoTime) {
        for (@NonNull final IsUpdatable component : components) {
            component.update(elapsedNanoTime);
        }
    }

}
