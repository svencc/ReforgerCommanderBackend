package com.recom.tacview.engine.renderables.mergeable;

import com.recom.tacview.engine.graphics.Bufferable;
import com.recom.tacview.engine.graphics.buffer.PixelBuffer;
import com.recom.tacview.engine.graphics.scanable.ScanableNoise;
import com.recom.tacview.engine.renderer.RenderProvider;
import com.recom.tacview.engine.units.PixelDimension;
import com.recom.tacview.service.RandomProvider;
import lombok.NonNull;

public class ScanableNoiseMergeable extends ScanableMergeableTemplate {

    @NonNull
    private final RenderProvider renderProvider;
    @NonNull
    private final ScanableNoise scanableNoiseDelegate;

    public ScanableNoiseMergeable(
            @NonNull final RenderProvider renderProvider,
            @NonNull final PixelDimension dimension,
            @NonNull final RandomProvider randomProvider
    ) {
        super(dimension);
        this.renderProvider = renderProvider;
        this.scanableNoiseDelegate = new ScanableNoise(randomProvider, dimension);
    }

    @Override
    public void mergeBufferWith(
            @NonNull final PixelBuffer targetBuffer,
            final int offsetX,
            final int offsetY
    ) {
        renderProvider.provide().render(scanableNoiseDelegate, targetBuffer, offsetX, offsetY);
        targetBuffer.setDirty(true);
    }

    @Override
    public void mergeBufferWith(
            @NonNull final Bufferable targetBuffer,
            final int offsetX,
            final int offsetY
    ) {
        renderProvider.provide().render(scanableNoiseDelegate, targetBuffer, offsetX, offsetY);
        targetBuffer.setDirty(true);
    }

    @Override
    public void prepareBuffer() {
        // nothing to render here, as wie have no buffer and the scanned pixel is generated randomly while scanning it.
    }

    @Override
    public void disposeBuffer() {
        // nothing to dispose here, as we have no buffer and the scanned pixel is generated randomly while scanning it.
    }

    @Override
    public int scanPixelAt(
            final int x,
            final int y
    ) {
        return scanableNoiseDelegate.scanPixelAt(x, y);
    }

    @Override
    public int scanPixelAtIndex(final int index) {
        return scanableNoiseDelegate.scanPixelAtIndex(index);
    }

}
