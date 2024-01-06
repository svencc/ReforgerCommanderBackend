package com.recom.tacview.engine.entity.component;

public abstract class PhysicComponent extends ComponentTemplate {

    @Override
    public ComponentType componentType() {
        return ComponentType.PHYSICS;
    }

    public PhysicComponent() {
        super(ComponentType.PhysicComponent);
    }

}
