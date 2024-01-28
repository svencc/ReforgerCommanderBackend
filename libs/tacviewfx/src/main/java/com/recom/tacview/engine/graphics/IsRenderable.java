package com.recom.tacview.engine.graphics;

import com.recom.tacview.engine.renderables.IsMergeable;
import com.recom.tacview.engine.graphics.buffer.PixelBuffer;
import lombok.NonNull;

public interface IsRenderable {

    void render(
            @NonNull final IsScanable sourceScanable,
            @NonNull final IsBufferable targetBuffer,
            final int xOffset,
            final int yOffset
    );

    void renderMergeable(
            @NonNull final IsMergeable source,
            @NonNull final PixelBuffer targetBuffer,
            final int xOffset,
            final int yOffset
    );

    void renderMergeable(
            @NonNull final IsMergeable source,
            @NonNull final IsBufferable target,
            final int xOffset,
            final int yOffset
    );

    void setPixelAt(
            @NonNull final IsBufferable target,
            final int x,
            final int y,
            final int newPixelValue
    );

}
