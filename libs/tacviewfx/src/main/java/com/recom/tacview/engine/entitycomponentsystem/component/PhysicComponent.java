package com.recom.tacview.engine.entitycomponentsystem.component;

public class PhysicComponent extends ComponentTemplate {
    private static float COUNTER_FORCE = 100f;
    private static float DAMPING_COEFFICIENT = 10f;

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


                double newVelocityX = applyLinearCounterForce(physicsCoreComponent.getVelocityXComponent(), COUNTER_FORCE);
                double newVelocityY = applyLinearCounterForce(physicsCoreComponent.getVelocityYComponent(), COUNTER_FORCE );
                physicsCoreComponent.setVelocityXComponent(newVelocityX);
                physicsCoreComponent.setVelocityYComponent(newVelocityY);

//                double newVelocityX = applyQuadraticDamping(physicsCoreComponent.getVelocityXComponent(), elapsedSeconds);
//                double newVelocityY = applyQuadraticDamping(physicsCoreComponent.getVelocityYComponent(), elapsedSeconds);
//                physicsCoreComponent.setVelocityXComponent(newVelocityX);
//                physicsCoreComponent.setVelocityYComponent(newVelocityY);

                this.getEntity().<RenderableComponent>locateComponent(ComponentType.RenderableComponent).ifPresent(RenderableComponent::propagateDirtyStateToParent);
            }
        });
    }

    private double applyQuadraticDamping(
            double velocity,
            double elapsedSeconds
    ) {
        double dampingForce = Math.copySign(1, velocity) * DAMPING_COEFFICIENT * velocity;
        double newVelocity = velocity - (dampingForce * elapsedSeconds);

        if (Math.abs(newVelocity) < 1) {
            newVelocity = 0;
        }

        return newVelocity;
    }

    private double applyLinearCounterForce(
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

    private double elapsedSeconds(final long elapsedNanoTime) {
        return elapsedNanoTime / 1_000_000_000.0;
    }

}
