package com.recom.tacview.engine.entity.component;

import com.recom.tacview.engine.entity.Entity;
import com.recom.tacview.engine.graphics.buffer.PixelBuffer;
import com.recom.tacview.engine.renderables.HasPixelBuffer;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public class RenderableComponent extends Component implements HasPixelBuffer {

    @Setter
    @Getter
    private int layer = 0;

    @Getter
    @NonNull
    private PixelBuffer pixelBuffer;


    @Override
    public void update(
            @NonNull final Entity entity,
            final double elapsedNanoTime
    ) {
        // render the component
        // needs to talk with physics component (position, velocity)
    }

}
