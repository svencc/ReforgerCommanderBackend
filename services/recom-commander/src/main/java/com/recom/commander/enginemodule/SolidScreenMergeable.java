package com.recom.commander.enginemodule;

import com.recom.tacview.engine.components.HasPixelBuffer;
import com.recom.tacview.engine.components.Mergeable;
import com.recom.tacview.engine.components.Soilable;
import com.recom.tacview.engine.graphics.Bufferable;
import com.recom.tacview.engine.graphics.buffer.PixelBuffer;
import com.recom.tacview.engine.renderer.RenderProvider;
import com.recom.tacview.property.RendererProperties;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public class SolidScreenMergeable implements Mergeable, HasPixelBuffer, Soilable {

    @NonNull
    private final RenderProvider renderProvider;
    @Getter
    @NonNull
    private final PixelBuffer pixelBuffer;
    @Setter
    @Getter
    private boolean dirty = true;
    private final int backgroundColor;

    public SolidScreenMergeable(
            @NonNull final RendererProperties rendererProperties,
            @NonNull final RenderProvider renderProvider,
            final int backgroundColor
    ) {
        this.renderProvider = renderProvider;
        this.pixelBuffer = new PixelBuffer(rendererProperties.toRendererDimension());
        this.backgroundColor = backgroundColor;
    }

    @Override
    public void mergeBufferWith(
            @NonNull PixelBuffer targetBuffer,
            int offsetX, int offsetY
    ) {
        renderProvider.provide().render(pixelBuffer, targetBuffer, offsetX, offsetY);
    }

    @Override
    public void mergeBufferWith(
            @NonNull Bufferable targetBuffer,
            int offsetX,
            int offsetY
    ) {
        renderProvider.provide().render(pixelBuffer, targetBuffer, offsetX, offsetY);
    }

    @Override
    public void prepareBuffer() {
        if (dirty) {
            this.pixelBuffer.fillBuffer(backgroundColor);
            setDirty(false);
        }
    }

    @Override
    public void disposeBuffer() {

    }

}
