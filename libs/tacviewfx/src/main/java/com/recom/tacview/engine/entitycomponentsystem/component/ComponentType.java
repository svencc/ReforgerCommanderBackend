package com.recom.tacview.engine.entitycomponentsystem.component;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ComponentType {

    InputComponent(0, com.recom.tacview.engine.entitycomponentsystem.component.InputComponent.class),
    PhysicsCoreComponent(10000, PhysicCoreComponent.class),
    PhysicsComponent(20000, PhysicComponent.class),
    RenderableComponent(30000, RenderableComponent.class);


    @Getter
    private final int processingOrder;
    @Getter
    private final Class<? extends ComponentTemplate> componentClass;

}
