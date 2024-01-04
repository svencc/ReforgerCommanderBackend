package com.recom.tacview.engine.entity.component;

import com.recom.tacview.engine.entity.Entity;
import com.recom.tacview.engine.renderables.HasPixelBuffer;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Deprecated
public abstract class ViewportComponent extends ComponentBase implements HasPixelBuffer {

    @Getter
    @Setter
    private int positionX = 0;

    @Getter
    @Setter
    private int positionY = 0;

    @NonNull
    public ComponentType componentType() {
        return ComponentType.VIEWPORT;
    }


    @Override
    public void update(
            @NonNull final Entity entity,
            final long elapsedNanoTime
    ) {
        // render the component
        // needs to talk with physics component (position, velocity)
    }

}