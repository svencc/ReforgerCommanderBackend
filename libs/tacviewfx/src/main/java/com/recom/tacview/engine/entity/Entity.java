package com.recom.tacview.engine.entity;

import com.recom.tacview.engine.entity.component.Component;
import lombok.NonNull;

import java.util.*;
import java.util.stream.Collectors;

public abstract class Entity implements HasComponents {

    @NonNull
    private final List<Component> components = new ArrayList<>();

    @NonNull
    private Map<? extends Class<? extends Component>, List<Component>> componentMap = new HashMap<>();

    public void addComponent(@NonNull final Component component) {
        components.add(component);
        components.sort(Comparator.comparing(Component::getSortOrder));
        indexComponents();
    }

    public void addComponents(@NonNull final List<Component> components) {
        this.components.addAll(components.stream().filter(Objects::nonNull).toList());
        this.components.sort(Comparator.comparing(Component::getSortOrder));
        indexComponents();
    }

    protected void indexComponents() {
        componentMap = components.stream()
                .sorted(Comparator.comparing(Component::getSortOrder))
                .collect(Collectors.groupingBy(Component::getClass));
    }

    public void removeComponent(@NonNull final Component component) {
        components.remove(component);
//        components.sort(Comparator.comparing(Component::getSortIndex));
        indexComponents();
    }

    @NonNull
    @SuppressWarnings("unchecked")
    public <T extends Component> Optional<T> locateComponent(@NonNull final Class<T> componentClass) {
        if (componentMap.containsKey(componentClass)) {
            return Optional.of((T) componentMap.get(componentClass).get(0));
        } else {
            return Optional.empty();
        }
    }

    @NonNull
    @SuppressWarnings("unchecked")
    public <T extends Component> List<T> locateComponents(@NonNull final Class<T> componentClass) {
        if (componentMap.containsKey(componentClass)) {
            return Collections.unmodifiableList((List<? extends T>) componentMap.get(componentClass));
        } else {
            return Collections.emptyList();
        }
    }

    @NonNull
    public List<Component> hasComponents() {
        return Collections.unmodifiableList(components);
    }

    void update(final long elapsedNanoTime) {
        for (@NonNull final Component component : components) {
            component.update(this, elapsedNanoTime);
        }
    }

}
