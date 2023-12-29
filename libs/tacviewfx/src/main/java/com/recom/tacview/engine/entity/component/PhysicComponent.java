package com.recom.tacview.engine.entity.component;

import com.recom.tacview.engine.entity.Entity;
import lombok.NonNull;

public class PhysicComponent extends Component {

    public ComponentSortOrder componentSortOrder() {
        return ComponentSortOrder.PHYSICS;
    }

    @Override
    public void update(
            @NonNull final Entity entity,
            final double elapsedNanoTime
    ) {
        // move the component -> using physics engine ...
    }

}
