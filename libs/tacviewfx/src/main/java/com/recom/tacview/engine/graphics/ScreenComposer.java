package com.recom.tacview.engine.graphics;

import com.recom.tacview.engine.components.Mergeable;
import com.recom.tacview.engine.graphics.buffer.PixelBuffer;
import com.recom.tacview.property.RendererProperties;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.util.LinkedList;

@Component
public class ScreenComposer implements Composable {

    @NonNull
    private final RendererProperties rendererProperties;
    @NonNull
    private final PixelBuffer[] ringPixelBuffer;
    @Getter
    @NonNull
    private final LinkedList<Mergeable> layerPipeline = new LinkedList<>();
    @Getter
    private int currentPixelBuffer = 0;
    private boolean isBackBufferEmpty = true;

    public ScreenComposer(@NonNull final RendererProperties rendererProperties) {
        this.rendererProperties = rendererProperties;
        ringPixelBuffer = new PixelBuffer[rendererProperties.getComposer().getBackBufferSize()];
        for (int i = 0; i < ringPixelBuffer.length; i++) {
            ringPixelBuffer[i] = new PixelBuffer(rendererProperties.toRendererDimension());
        }
        prefillBackBuffer();
    }

    private void prefillBackBuffer() {
        if (isBackBufferEmpty) {
            // pre-fill backBuffer -1
            int renderIterations = rendererProperties.getComposer().getBackBufferSize() - 1;
            for (int i = 0; i < renderIterations; i++) {
                currentPixelBuffer().clearBuffer();

                layerPipeline.forEach(layer -> {
                    layer.prepareBuffer();
                    layer.mergeBufferWith(ringPixelBuffer[currentPixelBuffer], 0, 0);
                    layer.disposeBuffer();
                });

                // int the last iteration we do not move pointer!
                if (i < renderIterations - 1) {
                    nextBufferSegment();
                }
            }
            isBackBufferEmpty = false;
        }
    }

    @NonNull
    private PixelBuffer currentPixelBuffer() {
        return ringPixelBuffer[currentPixelBuffer];
    }

    private void nextBufferSegment() {
        currentPixelBuffer = (currentPixelBuffer + 1) % rendererProperties.getComposer().getBackBufferSize();
    }

    @Override
    public int compose() {
        nextBufferSegment();
        currentPixelBuffer().clearBuffer();
        // Todo parallelize rendering of layers.
        // merge Buffers from top layer to last layer!
        layerPipeline.forEach(layer -> {
            layer.prepareBuffer();
            layer.mergeBufferWith(ringPixelBuffer[currentPixelBuffer], 0, 0);
            layer.disposeBuffer();
        });

        return currentPixelBuffer;
    }

    @NonNull
    @Override
    public PixelBuffer getBackPixelBuffer(final int index) {
        return ringPixelBuffer[index];
    }

}
