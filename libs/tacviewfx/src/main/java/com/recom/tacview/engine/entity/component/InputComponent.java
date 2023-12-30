package com.recom.tacview.engine.entity.component;

import com.recom.tacview.engine.entity.Entity;
import lombok.NonNull;

public class InputComponent extends Component {

    @NonNull
    public ComponentType componentType() {
        return ComponentType.INPUT;
    }

    @Override
    public void update(
            @NonNull final Entity entity,
            final long elapsedNanoTime
    ) {
        entity.locateComponent(PhysicCoreComponent.class).ifPresent(physicCoreComponent -> {
            physicCoreComponent.setVelocityXComponent(physicCoreComponent.getVelocityXComponent() + 1.0);
            physicCoreComponent.setVelocityYComponent(physicCoreComponent.getVelocityYComponent() + 1.0);
        });

        /*
        KeyboardInput
        MouseInput
        -> Old AWT Implementations; refactor to JavaFX and pass them here via GameLoop!

        It reads the joystick to determine how to accelerate the baker. Then it resolves its new position with the physics engine. Finally, it draws Bjørn onto the screen.
         */
        // needs to talk with physics component (position, velocity)
    }

}
