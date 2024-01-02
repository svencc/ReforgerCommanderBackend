package com.recom.tacview.engine.entity;

import com.recom.tacview.engine.entity.component.ComponentBase;
import lombok.NonNull;

import java.util.*;
import java.util.stream.Collectors;

public class Entity implements Componentable, Updatable {

    @NonNull
    private final List<ComponentBase> components = new ArrayList<>();

    @NonNull
    private Map<? extends Class<? extends ComponentBase>, List<ComponentBase>> componentMap = new HashMap<>();

    public void addComponent(@NonNull final ComponentBase component) {
        components.add(component);
        components.sort(Comparator.comparing(ComponentBase::getSortOrder));
        indexComponents();
    }

    public void addComponents(@NonNull final List<ComponentBase> components) {
        this.components.addAll(components.stream().filter(Objects::nonNull).toList());
        this.components.sort(Comparator.comparing(ComponentBase::getSortOrder));
        indexComponents();
    }

    protected void indexComponents() {
        componentMap = components.stream()
                .sorted(Comparator.comparing(ComponentBase::getSortOrder))
                .collect(Collectors.groupingBy(ComponentBase::getClass));
    }

    public void removeComponent(@NonNull final ComponentBase component) {
        components.remove(component);
//        components.sort(Comparator.comparing(Component::getSortIndex));
        indexComponents();
    }

    @NonNull
    @SuppressWarnings("unchecked")
    public <T extends ComponentBase> Optional<T> locateComponent(@NonNull final Class<T> componentClass) {
        if (componentMap.containsKey(componentClass)) {
            return Optional.of((T) componentMap.get(componentClass).get(0));
        } else {
            return Optional.empty();
        }
    }

    @NonNull
    @SuppressWarnings("unchecked")
    public <T extends ComponentBase> List<T> locateComponents(@NonNull final Class<T> componentClass) {
        if (componentMap.containsKey(componentClass)) {
            return Collections.unmodifiableList((List<? extends T>) componentMap.get(componentClass));
        } else {
            return Collections.emptyList();
        }
    }

    @NonNull
    public List<ComponentBase> getComponents() {
        return Collections.unmodifiableList(components);
    }

    public void update(final long elapsedNanoTime) {
        for (@NonNull final ComponentBase component : components) {
            component.update(this, elapsedNanoTime);
        }
    }

}
