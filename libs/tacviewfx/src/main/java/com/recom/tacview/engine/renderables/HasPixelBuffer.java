package com.recom.tacview.engine.renderables;

import com.recom.tacview.engine.graphics.buffer.PixelBuffer;
import lombok.NonNull;

public interface HasPixelBuffer {

    @NonNull
    PixelBuffer getPixelBuffer();

}
