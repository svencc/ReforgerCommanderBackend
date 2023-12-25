package com.recom.dynamicproperties;

import com.recom.tacview.engine.components.sprite.SpriteAtlas;
import com.recom.tacview.engine.renderer.MultithreadedSoftwareRenderer;
import com.recom.tacview.engine.graphics.Scanable;
import com.recom.tacview.engine.graphics.buffer.PixelBuffer;
import com.recom.tacview.engine.units.PixelDimension;
import com.recom.tacview.property.RendererProperties;
import com.recom.tacview.service.argb.ARGBCalculatorProvider;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RequiredArgsConstructor
class MultithreadedSoftwareRendererTest {

    private RendererProperties rendererProperties;
    private ARGBCalculatorProvider argbCalculatorProvider;
    private MultithreadedSoftwareRenderer rendererToTest;
    private PixelBuffer targetBuffer;

    @BeforeEach
    void beforeEach() {
        rendererProperties = RendererProperties.builder().parallelizedRendering(true).threadPoolSize(2).build();
        argbCalculatorProvider = new ARGBCalculatorProvider();
        rendererToTest = new MultithreadedSoftwareRenderer(rendererProperties, argbCalculatorProvider);
    }

    @Test
    void renderBufferToBuffer() {
        // PREPARE
        targetBuffer = new PixelBuffer(PixelDimension.builder().widthX(1).heightY(1).build());
        final PixelBuffer sourceBuffer = new PixelBuffer(PixelDimension.builder().widthX(1).heightY(1).build());
        int sourceBufferContent = 0xFFffffff;
        sourceBuffer.fillBuffer(sourceBufferContent);

        // EXECUTE
        rendererToTest.render(sourceBuffer, targetBuffer, 0, 0);

        // ASSERT
        // render-buffer-to-buffer method only proxies PixelBuffer copy method! -> see PixelBufferTest
        assertEquals(sourceBufferContent, targetBuffer.scanPixelAt(0, 0));
    }

    @Test
    void renderScanableToBuffer() {
        // PREPARE
        targetBuffer = new PixelBuffer(PixelDimension.builder().widthX(2).heightY(2).build());
        final PixelBuffer sourceBuffer = new PixelBuffer(PixelDimension.builder().widthX(1).heightY(1).build());
        int sourceBufferContent = 0xFFffffff;
        sourceBuffer.fillBuffer(sourceBufferContent);

        // EXECUTE
        rendererToTest.render((Scanable) sourceBuffer, targetBuffer, 0, 0);

        // ASSERT
        assertEquals(sourceBufferContent, targetBuffer.scanPixelAt(0, 0));
        assertEquals(0, targetBuffer.scanPixelAt(1, 0));
        assertEquals(0, targetBuffer.scanPixelAt(0, 1));
        assertEquals(0, targetBuffer.scanPixelAt(1, 1));
    }

    @Test
    void renderScanableToBufferWithOffset() {
        // PREPARE
        targetBuffer = new PixelBuffer(PixelDimension.builder().widthX(2).heightY(2).build());
        final PixelBuffer sourceBuffer = new PixelBuffer(PixelDimension.builder().widthX(2).heightY(2).build());
        int sourceBufferContent = 0xFFffffff;
        sourceBuffer.fillBuffer(sourceBufferContent);

        // EXECUTE
        rendererToTest.render((Scanable) sourceBuffer, targetBuffer, 1, 1);

        // ASSERT
        assertEquals(0, targetBuffer.scanPixelAt(0, 0));
        assertEquals(0, targetBuffer.scanPixelAt(1, 0));
        assertEquals(0, targetBuffer.scanPixelAt(0, 1));
        assertEquals(sourceBufferContent, targetBuffer.scanPixelAt(1, 1));
    }

    @Test
    void renderScanableToBufferWithNegativeOffset() {
        // PREPARE
        targetBuffer = new PixelBuffer(PixelDimension.builder().widthX(2).heightY(2).build());
        final PixelBuffer sourceBuffer = new PixelBuffer(PixelDimension.builder().widthX(2).heightY(2).build());
        int sourceBufferContent = 0xFFffffff;
        sourceBuffer.fillBuffer(sourceBufferContent);

        // EXECUTE
        rendererToTest.render((Scanable) sourceBuffer, targetBuffer, -1, -1);

        // ASSERT
        assertEquals(sourceBufferContent, targetBuffer.scanPixelAt(0, 0));
        assertEquals(0, targetBuffer.scanPixelAt(1, 0));
        assertEquals(0, targetBuffer.scanPixelAt(0, 1));
        assertEquals(0, targetBuffer.scanPixelAt(1, 1));
    }

    @Test
    void setPixelAt() {
        // PREPARE
        targetBuffer = new PixelBuffer(PixelDimension.builder().widthX(2).heightY(2).build());
        int newPixelContent = 0xffffff;

        // EXECUTE
        rendererToTest.setPixelAt(targetBuffer, 1, 1, newPixelContent);

        // ASSERT
        assertEquals(0, targetBuffer.scanPixelAt(0, 0));
        assertEquals(0, targetBuffer.scanPixelAt(1, 0));
        assertEquals(0, targetBuffer.scanPixelAt(0, 1));
        assertEquals(newPixelContent, targetBuffer.scanPixelAt(1, 1));
    }

    @Test
    void setPixelAtOutOfBounds() {
        // PREPARE
        targetBuffer = new PixelBuffer(PixelDimension.builder().widthX(2).heightY(2).build());
        int newPixelContent = 0xffffff;

        // EXECUTE
        rendererToTest.setPixelAt(targetBuffer, 9, 9, newPixelContent);

        // ASSERT
        assertEquals(0, targetBuffer.scanPixelAt(0, 0));
        assertEquals(0, targetBuffer.scanPixelAt(1, 0));
        assertEquals(0, targetBuffer.scanPixelAt(0, 1));
        assertEquals(0, targetBuffer.scanPixelAt(1, 1));
    }

    @Test
    void renderHexWithTransparentColor() throws IOException {
        // PREPARE
        final PixelBuffer bufferWithTestImage = getPixelBufferFromFile("/assets/testHex62x32.png");
        final PixelBuffer bufferWithExpectedImage = getPixelBufferFromFile("/assets/expectedHex62x32.png");

        final PixelBuffer pixelBufferToRenderIn = new PixelBuffer(PixelDimension.builder().widthX(bufferWithTestImage.getDimension().getWidthX()).heightY(bufferWithTestImage.getDimension().getHeightY()).build());
        int black = -16777216;
        pixelBufferToRenderIn.fillBuffer(black);

        // EXECUTE
        rendererToTest.render(bufferWithTestImage, pixelBufferToRenderIn, 0, 0);

        // ASSERT
        for (int i = 0; i < bufferWithTestImage.getBufferSize(); i++) {
            assertEquals(pixelBufferToRenderIn.scanPixelAtIndex(i), bufferWithExpectedImage.scanPixelAtIndex(i));
        }
    }

    @NonNull
    private static PixelBuffer getPixelBufferFromFile(@NonNull String name) throws IOException {
        final BufferedImage image = ImageIO.read(SpriteAtlas.class.getResource(name));
        final int[] imageBufferArray = new int[image.getWidth() * image.getHeight()];

        // load image to int[] imageBufferArray
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), imageBufferArray, 0, image.getWidth());

        // create PixelBuffer from image int[]
        return new PixelBuffer(PixelDimension.builder().widthX(image.getWidth()).heightY(image.getHeight()).build(), imageBufferArray);
    }

    @Test
    @Disabled
    void renderMergeable() {
    }

    @Test
    @Disabled
    void testRenderMergeable() {
    }

}