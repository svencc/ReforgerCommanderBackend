package com.recom.tacview.engine;

import com.recom.observer.ReactiveObserver;
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
//    @NonNull
//    final ReactiveObserver<IsEngineProperties> enginePropertiesReactiveObserver;
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

        intBuffer = IntBuffer.allocate(engineProperties.getRendererWidth() * engineProperties.getRendererHeight());
        pixelFormat = PixelFormat.getIntArgbPreInstance();
        imagePixelBuffer = new PixelBuffer<>(engineProperties.getRendererWidth(), engineProperties.getRendererHeight(), intBuffer, pixelFormat);
        img = new WritableImage(imagePixelBuffer);

        // die engine muss die loop stoppen, wenn die properties sich ändern
        // und dann die neuen properties propagieren
        // framebuffer + composer buffer müssen sich anpassen (ggf auch die einzelnen layer)
        // die engine muss die loop wieder starten
        /*
        enginePropertiesReactiveObserver = ReactiveObserver.reactWith((
                @NonNull final Subjective<IsEngineProperties> __,
                @NonNull final Notification<IsEngineProperties> notification
        ) -> {
            final IsEngineProperties properties = notification.getPayload();
            if (properties.getRendererHeight() == 0 || properties.getRendererWidth() == 0) {
                return;
            } else {
                intBuffer = IntBuffer.allocate(properties.getRendererWidth() * properties.getRendererHeight());
                imagePixelBuffer = new PixelBuffer<>(properties.getRendererWidth(), properties.getRendererHeight(), intBuffer, pixelFormat);
                img = new WritableImage(imagePixelBuffer);
            }
        });
        enginePropertiesReactiveObserver.observe(engineProperties.getBufferedSubject());
         */
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

}
