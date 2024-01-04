package com.recom.tacview.engine.graphics.buffer;

import com.recom.tacview.engine.units.PixelDimension;

public class NullPixelBuffer extends PixelBuffer {

    private static PixelBuffer instance;

    private NullPixelBuffer() {
        super(PixelDimension.of(0, 0));
    }

    public static NullPixelBuffer CREATE() {
        if (instance == null) {
            instance = new NullPixelBuffer();
        }

        return new NullPixelBuffer();
    }

}
