package com.recom.tacview.engine.entity.component;

import com.recom.tacview.engine.entity.Entity;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Setter
@Getter
public class PhysicCoreComponent extends Component {

    private double positionX = 0.0;

    private double positionY = 0.0;

    private double velocityXComponent = 0.0;

    private double velocityYComponent = 0.0;

    private double massInKg = 1.0;

    private double dragInNewton = 1.0;


    public ComponentSortOrder componentSortOrder() {
        return ComponentSortOrder.PHYSIC_CORE;
    }

    @Override
    public void update(
            @NonNull final Entity entity,
            final double elapsedNanoTime
    ) {

    }

}
