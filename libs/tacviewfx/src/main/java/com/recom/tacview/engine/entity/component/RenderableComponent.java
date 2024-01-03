package com.recom.tacview.engine.entity.component;

import com.recom.tacview.engine.entity.Entity;
import com.recom.tacview.engine.graphics.buffer.PixelBuffer;
import com.recom.tacview.engine.renderables.HasPixelBuffer;
import com.recom.tacview.engine.units.PixelDimension;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public abstract class RenderableComponent extends ComponentBase implements HasPixelBuffer {

    @Setter
    @Getter
    private int layer = 0;

    @Getter
    @NonNull
    protected PixelBuffer pixelBuffer = new PixelBuffer(PixelDimension.of(0, 0));

    @NonNull
    public ComponentType componentType() {
        return ComponentType.RENDER_FOREGROUND;
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
