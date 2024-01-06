package com.recom.tacview.engine.entity;

import com.recom.tacview.engine.Updatable;
import com.recom.tacview.engine.entity.interfaces.IsComponent;
import com.recom.tacview.engine.entity.interfaces.IsEntity;
import lombok.NonNull;

import java.util.*;
import java.util.stream.Collectors;

public class Entity implements IsEntity {

    @NonNull
    private final List<IsComponent> components = new ArrayList<>();

    @NonNull
    private Map<? extends Class<? extends IsComponent>, List<IsComponent>> componentMap = new HashMap<>();

    @Override
    public void addComponent(@NonNull final IsComponent component) {
        components.add(component);
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
                .collect(Collectors.groupingBy(IsComponent::getComponentClass));
    }

    @Override
    public void removeComponent(@NonNull final IsComponent component) {
        components.remove(component);
        reIndexComponents();
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends IsComponent> Optional<T> locateComponent(@NonNull final Class<T> componentClass) {
        if (componentMap.containsKey(componentClass)) {
            return Optional.of((T) componentMap.get(componentClass).get(0));
        } else {
            return Optional.empty();
        }
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends IsComponent> List<T> locateComponents(@NonNull final Class<T> componentClass) {
        if (componentMap.containsKey(componentClass)) {
            return Collections.unmodifiableList((List<? extends T>) componentMap.get(componentClass));
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
        for (@NonNull final Updatable component : components) {
            component.update(elapsedNanoTime);
        }
    }

}
