package com.recom.tacview.engine.graphics.buffer;

import com.recom.tacview.engine.units.PixelDimension;

public class NullPixelBuffer extends PixelBuffer {

    public static PixelBuffer INSTANCE = new NullPixelBuffer();

    private NullPixelBuffer() {
        super(PixelDimension.of(0, 0));
    }

}
