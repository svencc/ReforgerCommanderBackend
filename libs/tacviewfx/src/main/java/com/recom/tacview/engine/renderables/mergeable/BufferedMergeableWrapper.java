package com.recom.tacview.engine.renderables.mergeable;

import com.recom.tacview.engine.graphics.buffer.PixelBuffer;
import com.recom.tacview.engine.renderables.HasPixelBuffer;
import com.recom.tacview.engine.renderer.RenderProvider;
import lombok.NonNull;

public class BufferedMergeableWrapper extends BufferedMergeableTemplate {

    public BufferedMergeableWrapper(
            @NonNull final PixelBuffer pixelBuffer,
            @NonNull final RenderProvider renderProvider
    ) {
        super(pixelBuffer, renderProvider);
        getPixelBuffer().setDirty(true);
    }

    public BufferedMergeableWrapper(
            @NonNull final HasPixelBuffer bufferable,
            @NonNull final RenderProvider renderProvider
    ) {
        super(bufferable.getPixelBuffer(), renderProvider);
        getPixelBuffer().setDirty(true);
    }

    @Override
    public void prepareBuffer() {
        if (isDirty()) {
            getPixelBuffer().setDirty(false);
        }
    }

}
