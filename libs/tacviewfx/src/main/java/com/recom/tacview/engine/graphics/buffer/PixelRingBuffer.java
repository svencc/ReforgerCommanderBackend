package com.recom.tacview.engine.graphics.buffer;

import com.recom.commons.units.PixelDimension;
import com.recom.commons.units.ResizeCommand;
import com.recom.tacview.engine.renderables.HasPixelBuffer;
import lombok.Getter;
import lombok.NonNull;

public class PixelRingBuffer implements HasPixelBuffer {

    @NonNull
    protected PixelDimension dimension;
    @NonNull
    protected final PixelBuffer[] pixelBufferRing;
    @Getter
    private int currentBufferIndex;
    @Getter
    private int previouslyFinishedBufferIndex;


    public PixelRingBuffer(
            @NonNull final PixelDimension dimension,
            @NonNull final int capacity
    ) {
        this.dimension = dimension;
        this.pixelBufferRing = new PixelBuffer[capacity];
        this.currentBufferIndex = 0;
        this.previouslyFinishedBufferIndex = capacity - 1;

        preInitialize(capacity);
    }

    private void preInitialize(@NonNull final int capacity) {
        for (int i = 0; i < capacity; i++) {
            pixelBufferRing[i] = new PixelBuffer(dimension);
        }
        currentBufferIndex = 0;
        this.previouslyFinishedBufferIndex = capacity - 1;
    }

    public void next() {
        previouslyFinishedBufferIndex = currentBufferIndex;
        currentBufferIndex = (currentBufferIndex + 1) % pixelBufferRing.length;
        getPixelBuffer().setDirty(true);
    }

    @NonNull
    @Override
    public PixelBuffer getPixelBuffer() {
        return pixelBufferRing[currentBufferIndex];
    }

    @NonNull
    public PixelBuffer getPreviouslyFinishedPixelBuffer() {
        return pixelBufferRing[previouslyFinishedBufferIndex];
    }

    public void resizeBuffer(final ResizeCommand resizeCommand) {
        this.dimension = resizeCommand.getPixelDimension();
        for (int i = 0; i < pixelBufferRing.length; i++) {
            pixelBufferRing[i] = new PixelBuffer(this.dimension);
        }
    }

}
