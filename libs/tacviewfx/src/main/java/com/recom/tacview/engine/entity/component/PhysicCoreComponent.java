package com.recom.tacview.engine.entity.component;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PhysicCoreComponent extends ComponentTemplate {

    private double positionX = 0.0;

    private double positionY = 0.0;

    private double velocityXComponent = 0.0;

    private double velocityYComponent = 0.0;

    private double massInKg = 1.0;

    private double dragInNewton = 1.0;

    @Override
    public ComponentType componentType() {
        return ComponentType.PHYSIC_CORE;
    }

    public PhysicCoreComponent() {
        super(ComponentType.PhysicCoreComponent);
    }

}
