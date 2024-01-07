package com.recom.tacview.engine.graphics.buffer;

import com.recom.tacview.engine.renderables.HasPixelBuffer;
import com.recom.tacview.engine.units.PixelDimension;
import lombok.Getter;
import lombok.NonNull;

public class PixelRingBuffer implements HasPixelBuffer {

    @NonNull
    protected PixelDimension dimension;
    @NonNull
    protected final PixelBuffer[] pixelBufferRing;
    @Getter
    private int currentBufferIndex;


    public PixelRingBuffer(
            @NonNull final PixelDimension dimension,
            @NonNull final int capacity
    ) {
        this.dimension = dimension;
        this.pixelBufferRing = new PixelBuffer[capacity];
        this.currentBufferIndex = 0;

        preInitialize(capacity);
    }

    private void preInitialize(@NonNull final int capacity) {
        for (int i = 0; i < capacity; i++) {
            pixelBufferRing[i] = new PixelBuffer(dimension);
        }
        currentBufferIndex = 0;
    }

    public void next() {
        currentBufferIndex = (currentBufferIndex + 1) % pixelBufferRing.length;
        getPixelBuffer().setDirty(true);
    }

    @NonNull
    @Override
    public PixelBuffer getPixelBuffer() {
        return pixelBufferRing[currentBufferIndex];
    }

    // @TODO: >>>> to interface and further implement
    public void updateDimension(@NonNull final PixelDimension dimension) {
        this.dimension = dimension;
        for (int i = 0; i < pixelBufferRing.length; i++) {
            pixelBufferRing[i] = new PixelBuffer(dimension);
            pixelBufferRing[i].setDirty(true);
        }
    }

}
