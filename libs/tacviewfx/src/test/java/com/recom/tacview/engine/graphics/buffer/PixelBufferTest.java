package com.recom.tacview.engine.graphics.buffer;

import com.recom.tacview.engine.units.PixelDimension;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PixelBufferTest {

    private static void testCopiedToBiggerBuffer(PixelBuffer sourceBuffer, PixelDimension sourceDimension, int sourceBufferContent, PixelBuffer targetBuffer, int targetBufferContent) {
        for (int y = 0; y < sourceBuffer.getDimension().getHeightY(); y++) {
            for (int x = 0; x < sourceBuffer.getDimension().getWidthX(); x++) {
                if (x < sourceDimension.getWidthX() && x < targetBuffer.getDimension().getWidthX() && y < sourceDimension.getHeightY() && y < targetBuffer.getDimension().getHeightY()) {
                    assertEquals(sourceBufferContent, targetBuffer.scanPixelAt(x, y));
                } else {
                    assertEquals(targetBufferContent, targetBuffer.scanPixelAt(x, y));
                }
            }
        }
    }

    @Test
    void getBufferSize() {
        // PREPARE
        final PixelBuffer pixelBufferToTest = new PixelBuffer(PixelDimension.builder().heightY(10).widthX(10).build());
        // EXECUTE & ASSERT
        assertEquals(100, pixelBufferToTest.getBufferSize());
    }

    @Test
    void sizeAndFillTest() {
        // PREPARE
        final PixelBuffer pixelBufferToTest = new PixelBuffer(PixelDimension.builder().heightY(10).widthX(10).build());

        // EXECUTE
        pixelBufferToTest.fillBuffer(0x080808);

        // ASSERT
        for (int i = 0; i < pixelBufferToTest.getBufferSize(); i++) {
            assertEquals(0x080808, pixelBufferToTest.scanPixelAtIndex(i));
        }
    }

    @Test
    void clearBuffer() {
        // PREPARE
        final PixelBuffer pixelBufferToTest = new PixelBuffer(PixelDimension.builder().heightY(10).widthX(10).build());
        pixelBufferToTest.fillBuffer(0x080808);

        // EXECUTE
        pixelBufferToTest.clearBuffer();

        // ASSERT
        for (int i = 0; i < pixelBufferToTest.getBufferSize(); i++) {
            assertEquals(0xff000000, pixelBufferToTest.scanPixelAtIndex(i));
        }
    }

    @Test
    void resizeBufferToBigger() {
        // PREPARE
        final PixelDimension oldPixelDimension = PixelDimension.builder().heightY(2).widthX(2).build();
        final PixelBuffer bufferToTest = new PixelBuffer(oldPixelDimension);
        int oldBufferContent = 0x080808;
        bufferToTest.fillBuffer(oldBufferContent);

        // EXECUTE
        final PixelDimension newPixelDimension = PixelDimension.builder().widthX(3).heightY(3).build();
        bufferToTest.resizeBuffer(newPixelDimension);

        // ASSERT
        assertEquals(newPixelDimension, bufferToTest.getDimension());
        assertEquals(oldBufferContent, bufferToTest.scanPixelAt(0, 0));
        assertEquals(oldBufferContent, bufferToTest.scanPixelAt(1, 0));
        assertEquals(oldBufferContent, bufferToTest.scanPixelAt(1, 0));
        assertEquals(oldBufferContent, bufferToTest.scanPixelAt(1, 1));

        assertEquals(0x000000, bufferToTest.scanPixelAt(2, 0));
        assertEquals(0x000000, bufferToTest.scanPixelAt(2, 1));
        assertEquals(0x000000, bufferToTest.scanPixelAt(0, 2));
        assertEquals(0x000000, bufferToTest.scanPixelAt(1, 2));
        assertEquals(0x000000, bufferToTest.scanPixelAt(2, 2));
    }

    @Test
    void resizeBufferToSmaller() {
        // PREPARE
        final PixelDimension oldPixelDimension = PixelDimension.builder().heightY(3).widthX(3).build();
        final PixelBuffer bufferToTest = new PixelBuffer(oldPixelDimension);
        int oldBufferContent = 0x080808;
        bufferToTest.fillBuffer(oldBufferContent);

        // EXECUTE
        final PixelDimension newPixelDimension = PixelDimension.builder().widthX(2).heightY(2).build();
        bufferToTest.resizeBuffer(newPixelDimension);

        // ASSERT
        assertEquals(newPixelDimension, bufferToTest.getDimension());
        assertEquals(oldBufferContent, bufferToTest.scanPixelAt(0, 0));
        assertEquals(oldBufferContent, bufferToTest.scanPixelAt(1, 0));
        assertEquals(oldBufferContent, bufferToTest.scanPixelAt(1, 0));
        assertEquals(oldBufferContent, bufferToTest.scanPixelAt(1, 1));
    }

    @Test
    void copyToBiggerTarget() {
        // PREPARE
        final PixelBuffer sourceBuffer = new PixelBuffer(PixelDimension.builder().heightY(2).widthX(2).build());
        int sourceBufferContent = 0x080808;
        sourceBuffer.fillBuffer(sourceBufferContent);

        PixelDimension targetDimension = PixelDimension.builder().widthX(3).heightY(3).build();
        final PixelBuffer targetBuffer = new PixelBuffer(targetDimension);
        int targetBufferContent = 0xffffff;
        targetBuffer.fillBuffer(targetBufferContent);

        // EXECUTE
        PixelBuffer.copy(sourceBuffer, targetBuffer);

        // ASSERT
        assertEquals(targetDimension, targetBuffer.getDimension());
        assertEquals(sourceBufferContent, targetBuffer.scanPixelAt(0, 0));
        assertEquals(sourceBufferContent, targetBuffer.scanPixelAt(1, 0));
        assertEquals(sourceBufferContent, targetBuffer.scanPixelAt(0, 1));
        assertEquals(sourceBufferContent, targetBuffer.scanPixelAt(1, 1));

        assertEquals(targetBufferContent, targetBuffer.scanPixelAt(2, 0));
        assertEquals(targetBufferContent, targetBuffer.scanPixelAt(2, 1));
        assertEquals(targetBufferContent, targetBuffer.scanPixelAt(0, 2));
        assertEquals(targetBufferContent, targetBuffer.scanPixelAt(1, 2));
        assertEquals(targetBufferContent, targetBuffer.scanPixelAt(2, 2));
    }

    @Test
    void copyToSmallerTarget() {
        // PREPARE
        final PixelBuffer sourceBuffer = new PixelBuffer(PixelDimension.builder().heightY(3).widthX(3).build());
        int sourceBufferContent = 0x080808;
        sourceBuffer.fillBuffer(sourceBufferContent);

        PixelDimension targetDimension = PixelDimension.builder().widthX(2).heightY(2).build();
        final PixelBuffer targetBuffer = new PixelBuffer(targetDimension);
        int targetBufferContent = 0xffffff;
        targetBuffer.fillBuffer(targetBufferContent);

        // EXECUTE
        PixelBuffer.copy(sourceBuffer, targetBuffer);

        // ASSERT
        assertEquals(targetDimension, targetBuffer.getDimension());
        assertEquals(sourceBufferContent, targetBuffer.scanPixelAt(0, 0));
        assertEquals(sourceBufferContent, targetBuffer.scanPixelAt(1, 0));
        assertEquals(sourceBufferContent, targetBuffer.scanPixelAt(0, 1));
        assertEquals(sourceBufferContent, targetBuffer.scanPixelAt(1, 1));
    }

    @Test
    void copyToDifferentSizedTarget() {
        // PREPARE
        final PixelBuffer sourceBuffer = new PixelBuffer(PixelDimension.builder().heightY(3).widthX(3).build());
        int sourceBufferContent = 0x080808;
        sourceBuffer.fillBuffer(sourceBufferContent);

        final PixelDimension targetBufferDimension = PixelDimension.builder().widthX(2).heightY(4).build();
        final PixelBuffer targetBuffer = new PixelBuffer(targetBufferDimension);
        int targetBufferContent = 0xffffff;
        targetBuffer.fillBuffer(targetBufferContent);

        // EXECUTE
        PixelBuffer.copy(sourceBuffer, targetBuffer);

        // ASSERT
        assertEquals(targetBufferDimension, targetBuffer.getDimension());
        assertEquals(sourceBufferContent, targetBuffer.scanPixelAt(0, 0));
        assertEquals(sourceBufferContent, targetBuffer.scanPixelAt(1, 0));
        assertEquals(sourceBufferContent, targetBuffer.scanPixelAt(0, 1));
        assertEquals(sourceBufferContent, targetBuffer.scanPixelAt(1, 1));
        assertEquals(sourceBufferContent, targetBuffer.scanPixelAt(0, 2));
        assertEquals(sourceBufferContent, targetBuffer.scanPixelAt(1, 2));

        assertEquals(targetBufferContent, targetBuffer.scanPixelAt(0, 3));
        assertEquals(targetBufferContent, targetBuffer.scanPixelAt(1, 3));
    }

    @Test
    void copyWithOffsetToBiggerBuffer() {
        // PREPARE
        final PixelBuffer sourceBuffer = new PixelBuffer(PixelDimension.builder().heightY(2).widthX(2).build());
        int sourceBufferContent = 0x080808;
        sourceBuffer.fillBuffer(sourceBufferContent);

        final PixelDimension targetBufferDimension = PixelDimension.builder().widthX(3).heightY(3).build();
        final PixelBuffer targetBuffer = new PixelBuffer(targetBufferDimension);
        int targetBufferContent = 0xffffff;
        targetBuffer.fillBuffer(targetBufferContent);

        // EXECUTE
        PixelBuffer.copyWithOffset(sourceBuffer, targetBuffer, 1, 1);

        // ASSERT
        assertEquals(targetBufferDimension, targetBuffer.getDimension());
        assertEquals(targetBufferContent, targetBuffer.scanPixelAt(0, 0));
        assertEquals(targetBufferContent, targetBuffer.scanPixelAt(1, 0));
        assertEquals(targetBufferContent, targetBuffer.scanPixelAt(2, 0));

        assertEquals(targetBufferContent, targetBuffer.scanPixelAt(0, 1));
        assertEquals(sourceBufferContent, targetBuffer.scanPixelAt(1, 1));
        assertEquals(sourceBufferContent, targetBuffer.scanPixelAt(2, 1));

        assertEquals(targetBufferContent, targetBuffer.scanPixelAt(0, 2));
        assertEquals(sourceBufferContent, targetBuffer.scanPixelAt(1, 2));
        assertEquals(sourceBufferContent, targetBuffer.scanPixelAt(2, 2));
    }

    @Test
    void copyWithOffsetToSmallerBuffer() {
        // PREPARE
        final PixelBuffer sourceBuffer = new PixelBuffer(PixelDimension.builder().heightY(2).widthX(2).build());
        int sourceBufferContent = 0x080808;
        sourceBuffer.fillBuffer(sourceBufferContent);

        final PixelDimension targetBufferDimension = PixelDimension.builder().widthX(3).heightY(3).build();
        final PixelBuffer targetBuffer = new PixelBuffer(targetBufferDimension);
        int targetBufferContent = 0xffffff;
        targetBuffer.fillBuffer(targetBufferContent);

        // EXECUTE
        PixelBuffer.copyWithOffset(sourceBuffer, targetBuffer, 2, 2);

        // ASSERT
        assertEquals(targetBufferDimension, targetBuffer.getDimension());
        assertEquals(targetBufferContent, targetBuffer.scanPixelAt(0, 0));
        assertEquals(targetBufferContent, targetBuffer.scanPixelAt(1, 0));
        assertEquals(targetBufferContent, targetBuffer.scanPixelAt(2, 0));

        assertEquals(targetBufferContent, targetBuffer.scanPixelAt(0, 1));
        assertEquals(targetBufferContent, targetBuffer.scanPixelAt(1, 1));
        assertEquals(targetBufferContent, targetBuffer.scanPixelAt(2, 1));

        assertEquals(targetBufferContent, targetBuffer.scanPixelAt(0, 2));
        assertEquals(targetBufferContent, targetBuffer.scanPixelAt(1, 2));
        assertEquals(sourceBufferContent, targetBuffer.scanPixelAt(2, 2));
    }

    @Test
    void copyWithNegativeOffsetToSmallerBuffer() {
        // PREPARE
        final PixelBuffer sourceBuffer = new PixelBuffer(PixelDimension.builder().heightY(2).widthX(2).build());
        int sourceBufferContent = 0x080808;
        sourceBuffer.fillBuffer(sourceBufferContent);

        final PixelDimension targetBufferDimension = PixelDimension.builder().widthX(3).heightY(3).build();
        final PixelBuffer targetBuffer = new PixelBuffer(targetBufferDimension);
        int targetBufferContent = 0xffffff;
        targetBuffer.fillBuffer(targetBufferContent);

        // EXECUTE
        PixelBuffer.copyWithOffset(sourceBuffer, targetBuffer, -1, -1);

        // ASSERT
        assertEquals(targetBufferDimension, targetBuffer.getDimension());
        assertEquals(sourceBufferContent, targetBuffer.scanPixelAt(0, 0));
        assertEquals(targetBufferContent, targetBuffer.scanPixelAt(1, 0));
        assertEquals(targetBufferContent, targetBuffer.scanPixelAt(2, 0));

        assertEquals(targetBufferContent, targetBuffer.scanPixelAt(0, 1));
        assertEquals(targetBufferContent, targetBuffer.scanPixelAt(1, 1));
        assertEquals(targetBufferContent, targetBuffer.scanPixelAt(2, 1));

        assertEquals(targetBufferContent, targetBuffer.scanPixelAt(0, 2));
        assertEquals(targetBufferContent, targetBuffer.scanPixelAt(1, 2));
        assertEquals(targetBufferContent, targetBuffer.scanPixelAt(2, 2));
    }

}
