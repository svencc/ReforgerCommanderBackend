package com.recom.tacview.engine.entity.component;

import com.recom.tacview.engine.entity.Entity;
import lombok.NonNull;

public class InputComponent extends Component {

    @Override
    public void update(
            @NonNull final Entity entity,
            final double elapsedNanoTime
    ) {
        /*
        It reads the joystick to determine how to accelerate the baker. Then it resolves its new position with the physics engine. Finally, it draws Bjørn onto the screen.
         */
        // needs to talk with physics component (position, velocity)
    }

}
