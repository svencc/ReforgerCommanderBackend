package com.recom.tacview.engine;

import com.recom.tacview.engine.graphics.ScreenComposer;
import com.recom.tacview.property.RendererProperties;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.PixelBuffer;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import lombok.NonNull;

import java.nio.IntBuffer;

public class FrameBuffer {

    @NonNull
    private final Canvas canvas;
    @NonNull
    private final RendererProperties rendererProperties;
    @NonNull
    private final ScreenComposer screenComposer;


    private IntBuffer intBuffer = null;
    private PixelFormat<IntBuffer> pixelFormat = null;
    private PixelBuffer<IntBuffer> pixelBuffer = null;
    private WritableImage img = null;


    public int backBufferIndex = 0;


    public FrameBuffer(
            @NonNull final Canvas canvas,
            @NonNull final RendererProperties rendererProperties,
            @NonNull final ScreenComposer screenComposer
    ) {
        this.canvas = canvas;
        this.rendererProperties = rendererProperties;
        this.screenComposer = screenComposer;

        intBuffer = IntBuffer.allocate(rendererProperties.getWidth() * rendererProperties.getHeight());
        pixelFormat = PixelFormat.getIntArgbPreInstance();
        pixelBuffer = new PixelBuffer<>(rendererProperties.getWidth(), rendererProperties.getHeight(), intBuffer, pixelFormat);
        img = new WritableImage(pixelBuffer);
    }

    void swap() {
        pixelBuffer.getBuffer().put(screenComposer.getBackPixelBuffer(backBufferIndex).directBufferAccess());
        pixelBuffer.getBuffer().flip();
        pixelBuffer.updateBuffer(__ -> null);
        canvas.getGraphicsContext2D().drawImage(img, 0, 0);
    }

}
