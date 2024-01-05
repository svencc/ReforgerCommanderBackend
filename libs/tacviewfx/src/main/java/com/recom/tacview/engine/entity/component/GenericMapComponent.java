package com.recom.tacview.engine.entity.component;

import com.recom.tacview.engine.graphics.buffer.NullPixelBuffer;
import com.recom.tacview.engine.graphics.buffer.PixelBuffer;
import lombok.NonNull;

public abstract class GenericMapComponent extends GenericRenderableComponent {

    public GenericMapComponent() {
        super(ComponentType.MAP_LAYER, NullPixelBuffer.CREATE());
    }

    public GenericMapComponent(@NonNull final PixelBuffer pixelBuffer) {
        super(ComponentType.MAP_LAYER, pixelBuffer);
    }

}
