package com.recom.tacview.engine.graphics;

import com.recom.tacview.engine.graphics.buffer.PixelBuffer;
import lombok.NonNull;

public interface Composable {

    int compose();

    @NonNull
    PixelBuffer getBackPixelBuffer(final int index);

}