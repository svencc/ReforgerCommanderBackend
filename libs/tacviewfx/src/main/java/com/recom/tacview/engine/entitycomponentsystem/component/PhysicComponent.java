package com.recom.tacview.engine.entitycomponentsystem.component;

public class PhysicComponent extends ComponentTemplate {

    public PhysicComponent() {
        super(ComponentType.PhysicsComponent);
    }

    @Override
    public void update(final long elapsedNanoTime) {
        this.getEntity().<PhysicCoreComponent>locateComponent(ComponentType.PhysicsCoreComponent).ifPresent(physicsCoreComponent -> {
            if (physicsCoreComponent.getVelocityXComponent() == 0 && physicsCoreComponent.getVelocityYComponent() == 0) {
                return;
            } else {
                double elapsedSeconds = elapsedSeconds(elapsedNanoTime);

                double newPositionX = physicsCoreComponent.getPositionX() + (physicsCoreComponent.getVelocityXComponent() * elapsedSeconds);
                double newPositionY = physicsCoreComponent.getPositionY() + (physicsCoreComponent.getVelocityYComponent() * elapsedSeconds);
                physicsCoreComponent.setPositionX(newPositionX);
                physicsCoreComponent.setPositionY(newPositionY);

                final double restoringForce = physicsCoreComponent.getRestoringForce();
                if (restoringForce != 0) {
                    double newVelocityX = applyRestoringForce(physicsCoreComponent.getVelocityXComponent(), restoringForce);
                    double newVelocityY = applyRestoringForce(physicsCoreComponent.getVelocityYComponent(), restoringForce);
                    physicsCoreComponent.setVelocityXComponent(newVelocityX);
                    physicsCoreComponent.setVelocityYComponent(newVelocityY);
                }
                this.getEntity().<RenderableComponent>locateComponent(ComponentType.RenderableComponent).ifPresent(RenderableComponent::propagateDirtyStateToParent);
            }
        });
    }

    private double elapsedSeconds(final long elapsedNanoTime) {
        return elapsedNanoTime / 1_000_000_000.0;
    }

    private double applyRestoringForce(
            final double velocity,
            final double counterForce
    ) {
        if (velocity > 0) {
            return Math.max(0, velocity - counterForce);
        } else if (velocity < 0) {
            return Math.min(0, velocity + counterForce);
        } else {
            return 0;
        }
    }

}
