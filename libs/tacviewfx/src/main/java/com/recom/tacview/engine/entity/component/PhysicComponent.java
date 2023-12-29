package com.recom.tacview.engine.entity.component;

import com.recom.tacview.engine.entity.Entity;
import lombok.NonNull;

public class PhysicComponent extends Component {

    /*
    --------------------
    pan component: move to entity
        position x
        position y

        velocityXComponent
        velocityYComponent
    --------------------



    --------------------
    physic component related values
        mass
        drag (luftwiederstand)
        https://en.wikipedia.org/wiki/Drag_(physics)
    --------------------
     */

    @Override
    public void update(
            @NonNull final Entity entity,
            final double elapsedNanoTime
    ) {
        // move the component -> using physics engine ...
    }

}
