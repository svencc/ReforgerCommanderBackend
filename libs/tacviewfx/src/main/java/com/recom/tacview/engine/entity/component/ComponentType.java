package com.recom.tacview.engine.entity.component;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ComponentType {

    INPUT(0, InputComponent.class),
    PHYSIC_CORE(10000, PhysicCoreComponent.class),
    PHYSICS(20000, PhysicComponent.class),
    RENDERABLE(30000, RenderableComponent.class);


    @Getter
    private final int processingOrder;
    @Getter
    private final Class<? extends ComponentTemplate> componentClass;

    public static Class<InputComponent> InputComponent = InputComponent.class;
    public static Class<PhysicComponent> PhysicComponent = PhysicComponent.class;
    public static Class<PhysicCoreComponent> PhysicCoreComponent = PhysicCoreComponent.class;
    public static Class<RenderableComponent> RenderableComponent = RenderableComponent.class;

}
