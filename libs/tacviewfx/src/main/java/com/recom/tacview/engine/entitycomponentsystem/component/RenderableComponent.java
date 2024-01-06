package com.recom.tacview.engine.entitycomponentsystem.component;

import com.recom.tacview.engine.graphics.buffer.NullPixelBuffer;
import com.recom.tacview.engine.graphics.buffer.PixelBuffer;
import com.recom.tacview.engine.renderables.HasPixelBuffer;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Setter
@Getter
public abstract class RenderableComponent extends ComponentTemplate implements HasPixelBuffer {

    private int zIndex = 0;

    @NonNull
    protected PixelBuffer pixelBuffer;

    public RenderableComponent() {
        super(ComponentType.RenderableComponent);
        this.pixelBuffer = NullPixelBuffer.CREATE();
    }

    public RenderableComponent(@NonNull final PixelBuffer pixelBuffer) {
        super(ComponentType.RenderableComponent);
        this.pixelBuffer = pixelBuffer;
    }

}
