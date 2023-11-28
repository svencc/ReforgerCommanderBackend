package com.recom.tacview.engine.graphics.buffer;

import com.recom.tacview.engine.graphics.Bufferable;
import com.recom.tacview.engine.units.PixelDimension;
import lombok.Getter;
import lombok.NonNull;

import java.util.Arrays;

public class PixelBuffer implements Bufferable {

    @Getter
    protected PixelDimension dimension;

    protected int[] pixelBuffer;

    public PixelBuffer(@NonNull final PixelDimension dimension) {
        this.dimension = dimension;
        pixelBuffer = new int[dimension.getWidthX() * dimension.getHeightY()];
    }

    public PixelBuffer(
            @NonNull final PixelDimension dimension,
            final int[] useBuffer
    ) {
        this.dimension = dimension;
        pixelBuffer = useBuffer;
    }

    public static void copy(
            @NonNull final PixelBuffer sourcePixelBuffer,
            @NonNull final PixelBuffer targetPixelBuffer
    ) {
        copyWithOffset(sourcePixelBuffer, targetPixelBuffer, 0, 0);
    }

    public static void copyWithOffset(
            @NonNull final PixelBuffer sourcePixelBuffer,
            @NonNull final PixelBuffer targetPixelBuffer,
            int offsetX,
            int offsetY
    ) {
        for (int y = 0; (y < targetPixelBuffer.dimension.getHeightY() && y < sourcePixelBuffer.dimension.getHeightY()); y++) {
            int copyToY = y + offsetY;
            if (copyToY >= targetPixelBuffer.dimension.getHeightY()) break;
            if (copyToY < 0) continue;
            for (int x = 0; (x < targetPixelBuffer.dimension.getWidthX() && x < sourcePixelBuffer.dimension.getWidthX()); x++) {
                int copyToX = x + offsetX;
                if (copyToX >= targetPixelBuffer.dimension.getWidthX()) break;
                if (copyToX < 0) continue;
                targetPixelBuffer.pixelBuffer[copyToX + copyToY * targetPixelBuffer.dimension.getWidthX()] = sourcePixelBuffer.pixelBuffer[x + y * sourcePixelBuffer.dimension.getWidthX()];
            }
        }
    }

    public void resizeBuffer(@NonNull final PixelDimension newDimension) {
        if (dimension.getWidthX() == newDimension.getWidthX() && dimension.getHeightY() == newDimension.getHeightY()) {
            return;
        }

        final PixelDimension oldPixelDimension = dimension;
        int[] oldPixelBuffer = pixelBuffer;

        int[] newPixelBuffer = new int[newDimension.getWidthX() * newDimension.getHeightY()];
        copy(oldPixelDimension, oldPixelBuffer, newDimension, newPixelBuffer);

        pixelBuffer = newPixelBuffer;
        dimension = newDimension;
    }

    public static void copy(
            @NonNull final PixelDimension sourceDimension,
            final int[] sourceBuffer,
            @NonNull final PixelDimension targetDimension,
            final int[] targetBuffer
    ) {
        for (int y = 0; (y < targetDimension.getHeightY() && y < sourceDimension.getHeightY()); y++) {
            for (int x = 0; x < targetDimension.getWidthX() && x < sourceDimension.getWidthX(); x++) {
                targetBuffer[x + y * targetDimension.getWidthX()] = sourceBuffer[x + y * sourceDimension.getWidthX()];
            }
        }
    }

    @Override
    public int scanPixelAt(
            final int x,
            final int y
    ) {
        return pixelBuffer[x + y * dimension.getWidthX()];
    }

    @Override
    public int scanPixelAtIndex(final int index) {
        return pixelBuffer[index];
    }

    @Override
    public void bufferPixelAt(
            final int x,
            final int y,
            final int newPixelValue
    ) {
        pixelBuffer[x + y * dimension.getWidthX()] = newPixelValue;
    }

    @Override
    public void bufferPixelAtIndex(
            final int index,
            final int newPixelValue
    ) {
        pixelBuffer[index] = newPixelValue;
    }

    public void clearBuffer() {
        Arrays.fill(pixelBuffer, 0);
    }

    public void fillBuffer(final int value) {
        Arrays.fill(pixelBuffer, value);
    }

    @Override
    public int[] directBufferAccess() {
        return pixelBuffer;
    }

    @Override
    public int getBufferSize() {
        return pixelBuffer.length;
    }

}
