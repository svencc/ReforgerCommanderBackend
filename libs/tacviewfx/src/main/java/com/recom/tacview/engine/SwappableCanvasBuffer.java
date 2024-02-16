package com.recom.tacview.engine;

import com.recom.commons.units.ResizeCommand;
import com.recom.tacview.engine.graphics.ScreenComposer;
import com.recom.tacview.property.IsEngineProperties;
import jakarta.annotation.Nullable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
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
    @NonNull
    private final PixelFormat<IntBuffer> pixelFormat;
    @Nullable
    private IntBuffer intBuffer = null;
    @Nullable
    private PixelBuffer<IntBuffer> imagePixelBuffer = null;
    @Nullable
    private WritableImage img = null;
    public int currentShownBackBufferIndex = -1;


    public SwappableCanvasBuffer(
            @NonNull final Canvas canvas,
            @NonNull final IsEngineProperties engineProperties,
            @NonNull final ScreenComposer screenComposer
    ) {
        this.canvas = canvas;
        this.screenComposer = screenComposer;

        pixelFormat = PixelFormat.getIntArgbPreInstance();
        intBuffer = IntBuffer.allocate(engineProperties.getRendererWidth() * engineProperties.getRendererHeight());
        imagePixelBuffer = new PixelBuffer<>(engineProperties.getRendererWidth(), engineProperties.getRendererHeight(), intBuffer, pixelFormat);
        img = new WritableImage(imagePixelBuffer);
    }

    void swap() {
        // swap the back buffer, and the front buffer
        int previouslyFinishedBufferIndex = screenComposer.getPreviouslyFinishedBufferIndex();
        if (currentShownBackBufferIndex == previouslyFinishedBufferIndex) {
            // if the buffers are the same, do nothing
        } else {
            // else swap the buffers
            currentShownBackBufferIndex = previouslyFinishedBufferIndex;
            imagePixelBuffer.getBuffer().put(screenComposer.getPreviouslyFinishedPixelBuffer().directBufferAccess());
            imagePixelBuffer.getBuffer().flip();
            imagePixelBuffer.updateBuffer(__ -> null);

            final GraphicsContext graphicsContext2D = canvas.getGraphicsContext2D();
            graphicsContext2D.drawImage(img, 0, 0, canvas.getWidth(), canvas.getHeight());
        }
    }

    public void resizeBuffer(final ResizeCommand resizeCommand) {
        intBuffer = IntBuffer.allocate(resizeCommand.getScaledPixelDimension().getWidthX() * resizeCommand.getScaledPixelDimension().getHeightY());
        imagePixelBuffer = new PixelBuffer<>(resizeCommand.getScaledPixelDimension().getWidthX(), resizeCommand.getScaledPixelDimension().getHeightY(), intBuffer, pixelFormat);
        img = new WritableImage(imagePixelBuffer);
    }

}
