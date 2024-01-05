package com.recom.tacview.engine.entity.component;

import com.recom.tacview.engine.graphics.buffer.PixelBuffer;
import com.recom.tacview.engine.renderables.HasPixelBuffer;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Setter
@Getter
public abstract class GenericRenderableComponent extends ComponentTemplate implements HasPixelBuffer {

    private int zIndex = 0;

    @NonNull
    protected PixelBuffer pixelBuffer;


    public GenericRenderableComponent(
            @NonNull final ComponentType componentType,
            @NonNull final PixelBuffer pixelBuffer
    ) {
        super(componentType);
        this.pixelBuffer = pixelBuffer;
    }

}
