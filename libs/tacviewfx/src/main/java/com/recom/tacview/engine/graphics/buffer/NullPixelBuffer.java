package com.recom.tacview.engine.graphics.buffer;

import com.recom.tacview.engine.units.PixelDimension;

// @TODO Da wäre vielleicht ein resizable PixelBuffer das richtige!?
// @TODO in abhängigkeit von den Render Properties .... die dürfen nicht mehr aus den spring properties kommen, sondern aus einer persistierbaren properties datei!
@Deprecated
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
