package com.recom.tacview.engine.renderables.mergeable;

import com.recom.tacview.engine.graphics.IsBufferable;
import com.recom.tacview.engine.graphics.buffer.PixelBuffer;
import com.recom.tacview.engine.renderables.HasPixelBuffer;
import com.recom.tacview.engine.renderables.IsMergeable;
import com.recom.tacview.engine.renderer.RenderProvider;
import com.recom.tacview.engine.units.PixelDimension;
import lombok.Getter;
import lombok.NonNull;


public abstract class BufferedMergeableTemplate implements IsMergeable, HasPixelBuffer {

    @Getter
    @NonNull
    private final PixelBuffer pixelBuffer;

    @NonNull
    private final RenderProvider renderProvider;

    public BufferedMergeableTemplate(
            @NonNull final PixelDimension dimension,
            @NonNull final RenderProvider renderProvider
    ) {
        this.pixelBuffer = new PixelBuffer(dimension);
        this.renderProvider = renderProvider;

        this.pixelBuffer.setDirty(true);
    }

    public BufferedMergeableTemplate(
            @NonNull final PixelBuffer fromPixelBuffer,
            @NonNull final RenderProvider renderProvider
    ) {
        this.pixelBuffer = fromPixelBuffer;
        this.renderProvider = renderProvider;
    }

    @Override
    public boolean isDirty() {
        return pixelBuffer.isDirty();
    }

    @Override
    public void setDirty(final boolean isDirty) {
        pixelBuffer.setDirty(isDirty);
    }

    @Override
    public void mergeBufferWith(
            @NonNull final PixelBuffer targetBuffer,
            final int offsetX,
            final int offsetY
    ) {
        renderProvider.provide().render(pixelBuffer, targetBuffer, offsetX, offsetY);
        targetBuffer.setDirty(true);
    }

    @Override
    public void mergeBufferWith(
            @NonNull final IsBufferable targetBuffer,
            final int offsetX,
            final int offsetY
    ) {
        renderProvider.provide().render(pixelBuffer, targetBuffer, offsetX, offsetY);
        targetBuffer.setDirty(true);
    }

    @Override
    public void prepareBuffer() {
        this.pixelBuffer.clearBuffer();
    }

    @Override
    public void dispose() {

    }

}
