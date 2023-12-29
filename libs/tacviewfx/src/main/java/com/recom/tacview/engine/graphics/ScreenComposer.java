package com.recom.tacview.engine.graphics;

import com.recom.tacview.engine.graphics.buffer.PixelBuffer;
import com.recom.tacview.engine.renderables.Mergeable;
import com.recom.tacview.property.RendererProperties;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class ScreenComposer implements Composable {

    @NonNull
    private final RendererProperties rendererProperties;
    @NonNull
    private final ExecutorService multithreadedExecutorService;
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

//        multithreadedExecutorService = Executors.newFixedThreadPool(rendererProperties.getThreadPoolSize());
        multithreadedExecutorService = Executors.newVirtualThreadPerTaskExecutor();

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
        final boolean isPipelineDirty = layerPipeline.stream()
                .map(Mergeable::isDirty)
                .reduce(true, (first, second) -> first && second);

        if (!isPipelineDirty) {
            return currentPixelBuffer;
        } else {
            nextBufferSegment();
            currentPixelBuffer().clearBuffer();
            // Todo parallelize rendering of layers. (/)
            // merge Buffers from top layer to last layer! (/)
            renderLayerBuffersInParallel(); // prepare buffers in parallel
            layerPipeline.forEach(layer -> {
                // layer.prepareBuffer(); // do not prepare buffers sequentially
                layer.mergeBufferWith(ringPixelBuffer[currentPixelBuffer], 0, 0);
                layer.disposeBuffer();
            });

            return currentPixelBuffer;
        }
    }

    private void renderLayerBuffersInParallel() {
        final CountDownLatch latch = new CountDownLatch(layerPipeline.size());
        for (final Mergeable layer : layerPipeline) {
            multithreadedExecutorService.execute(() -> {
                try {
                    layer.prepareBuffer();
                } finally {
                    latch.countDown();
                }
            });
        }

        try {
            latch.await();
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public PixelBuffer getBackPixelBuffer(final int index) {
        return ringPixelBuffer[index];
    }

}
