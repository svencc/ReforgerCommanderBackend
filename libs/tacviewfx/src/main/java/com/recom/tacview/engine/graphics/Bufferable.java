package com.recom.tacview.engine.graphics;

import com.recom.tacview.engine.units.PixelDimension;

public interface Bufferable extends Scanable {

    PixelDimension getDimension();

    void bufferPixelAt(
            final int x,
            final int y,
            final int newPixelValue
    );

    void bufferPixelAtIndex(
            final int index,
            final int newPixelValue
    );

    void clearBuffer();

    void fillBuffer(final int value);

    int[] directBufferAccess();

    int getBufferSize();

}