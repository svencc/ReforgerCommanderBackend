package com.recom.tacview.engine.renderables;

import com.recom.tacview.engine.graphics.IsBufferable;
import com.recom.tacview.engine.graphics.buffer.PixelBuffer;
import lombok.NonNull;

public interface IsMergeable extends IsSoilable {

    boolean isDirty();
    
    void setDirty(final boolean isDirty);


    void mergeBufferWith(
            @NonNull final PixelBuffer targetBuffer,
            final int offsetX,
            final int offsetY
    );

    void mergeBufferWith(
            @NonNull final IsBufferable targetBuffer,
            final int offsetX,
            final int offsetY
    );

    void prepareBuffer();

    void dispose();

}
