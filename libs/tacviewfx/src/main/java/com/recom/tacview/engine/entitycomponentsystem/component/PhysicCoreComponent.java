package com.recom.tacview.engine.entitycomponentsystem.component;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PhysicCoreComponent extends ComponentTemplate {

    private double positionX = 0.0;

    private double positionY = 0.0;

    private double velocityXComponent = 0.0;

    private double velocityYComponent = 0.0;

    private double dragInNewton = 1.0;

    private double restoringForce = 0;


    public PhysicCoreComponent() {
        super(ComponentType.PhysicsCoreComponent);
    }

    public void addVelocityXComponent(final double velocityXComponent) {
        this.velocityXComponent += velocityXComponent;
    }

    public void addVelocityYComponent(final double velocityYComponent) {
        this.velocityYComponent += velocityYComponent;
    }

}
