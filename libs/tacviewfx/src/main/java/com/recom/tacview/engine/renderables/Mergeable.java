package com.recom.tacview.engine.renderables;

import com.recom.tacview.engine.graphics.Bufferable;
import com.recom.tacview.engine.graphics.buffer.PixelBuffer;
import lombok.NonNull;

public interface Mergeable extends Soilable {

    boolean isDirty();
    
    void setDirty(final boolean isDirty);


    void mergeBufferWith(
            @NonNull final PixelBuffer targetBuffer,
            final int offsetX,
            final int offsetY
    );

    void mergeBufferWith(
            @NonNull final Bufferable targetBuffer,
            final int offsetX,
            final int offsetY
    );

    void prepareBuffer();

    void disposeBuffer();

}
