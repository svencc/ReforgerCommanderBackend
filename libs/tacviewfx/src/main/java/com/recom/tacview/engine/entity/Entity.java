package com.recom.tacview.engine.entity;

import com.recom.tacview.engine.entity.component.Component;
import lombok.Getter;
import lombok.NonNull;

import java.util.List;

public abstract class Entity implements HasComponents {

    @Getter
    private double x = 0.0;
    @Getter
    private double y = 0.0;
    @Getter
    private List<Component> components;


    void update(final double elapsedNanoTime) {
        for (@NonNull final Component component : components) {
            component.update(this, elapsedNanoTime);
        }
    }

}
