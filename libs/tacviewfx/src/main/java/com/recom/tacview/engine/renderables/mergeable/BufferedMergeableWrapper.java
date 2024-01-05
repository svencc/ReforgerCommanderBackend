package com.recom.tacview.engine.renderables.mergeable;

import com.recom.tacview.engine.graphics.buffer.PixelBuffer;
import com.recom.tacview.engine.renderables.HasPixelBuffer;
import com.recom.tacview.engine.renderables.mergeable.template.BufferedMergeableTemplate;
import com.recom.tacview.engine.renderer.RenderProvider;
import lombok.NonNull;
import lombok.Setter;

public class BufferedMergeableWrapper extends BufferedMergeableTemplate {

    @Setter
    @NonNull
    private PrepareBufferRunnable prepareBufferRunnable = (final BufferedMergeableWrapper wrapper) -> {
    };
    @Setter
    @NonNull
    private DisposeRunnable disposeRunnable = (final BufferedMergeableWrapper wrapper) -> {
    };


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
        if (prepareBufferRunnable != null) {
            prepareBufferRunnable.run(this);
        }
    }

    @Override
    public void dispose() {
        if (disposeRunnable != null) {
            disposeRunnable.run(this);
        }
    }

    public interface PrepareBufferRunnable {
        void run(@NonNull final BufferedMergeableWrapper wrapper);
    }

    public interface DisposeRunnable {
        void run(@NonNull final BufferedMergeableWrapper wrapper);
    }

}
