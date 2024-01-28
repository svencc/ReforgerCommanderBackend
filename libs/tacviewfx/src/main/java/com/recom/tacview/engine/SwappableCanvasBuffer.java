package com.recom.tacview.engine;

import com.recom.tacview.engine.graphics.ScreenComposer;
import com.recom.tacview.property.RendererProperties;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.PixelBuffer;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import lombok.NonNull;

import java.nio.IntBuffer;

public class SwappableCanvasBuffer {

    @NonNull
    private final Canvas canvas;
    @NonNull
    private final ScreenComposer screenComposer;


    private IntBuffer intBuffer = null;
    private PixelFormat<IntBuffer> pixelFormat = null;
    private PixelBuffer<IntBuffer> pixelBuffer = null;
    private WritableImage img = null;


    public int currentBackBufferIndex = 0;
    public int lastBackBufferIndex = -1;


    public SwappableCanvasBuffer(
            @NonNull final Canvas canvas,
            @NonNull final RendererProperties rendererProperties,
            @NonNull final ScreenComposer screenComposer
    ) {
        this.canvas = canvas;
        this.screenComposer = screenComposer;

        intBuffer = IntBuffer.allocate(rendererProperties.getWidth() * rendererProperties.getHeight());
        pixelFormat = PixelFormat.getIntArgbPreInstance();
        pixelBuffer = new PixelBuffer<>(rendererProperties.getWidth(), rendererProperties.getHeight(), intBuffer, pixelFormat);
        img = new WritableImage(pixelBuffer);
    }

    void swap() {
        // swap the back buffer, and the front buffer
        if (currentBackBufferIndex == lastBackBufferIndex) {
            // if the buffers are the same, do nothing
        } else {
            // else swap the buffers
            lastBackBufferIndex = currentBackBufferIndex;
            pixelBuffer.getBuffer().put(screenComposer.getPixelBuffer().directBufferAccess());
            pixelBuffer.getBuffer().flip();
            pixelBuffer.updateBuffer(__ -> null);
            canvas.getGraphicsContext2D().drawImage(img, 0, 0);
        }
    }

}
