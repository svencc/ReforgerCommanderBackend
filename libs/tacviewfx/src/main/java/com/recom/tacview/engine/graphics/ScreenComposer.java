package com.recom.tacview.engine.graphics;

import com.recom.tacview.engine.graphics.buffer.PixelBuffer;
import com.recom.tacview.engine.graphics.buffer.PixelRingBuffer;
import com.recom.tacview.engine.renderables.Mergeable;
import com.recom.tacview.property.RendererProperties;
import com.recom.tacview.service.RendererExecutorProvider;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

@Slf4j
@Component
public class ScreenComposer implements Composable {

    @NonNull
    private final RendererProperties rendererProperties;
    @NonNull
    private final ExecutorService executorService;
    @NonNull
    private final PixelRingBuffer pixelRingBuffer;
    @Getter
    @NonNull
    private final LinkedList<Mergeable> layerPipeline = new LinkedList<>();
    private boolean isBackBufferEmpty = true;


    public ScreenComposer(
            @NonNull final RendererProperties rendererProperties,
            @NonNull final RendererExecutorProvider rendererExecutorProvider
    ) {
        this.rendererProperties = rendererProperties;
        this.executorService = rendererExecutorProvider.provideNewExecutor();
        this.pixelRingBuffer = new PixelRingBuffer(rendererProperties.toRendererDimension(), rendererProperties.getComposer().getBackBufferSize());

        prefillBackBuffer();
    }

    private void prefillBackBuffer() {
        if (isBackBufferEmpty) {
            // pre-fill backBuffer
            int renderIterations = rendererProperties.getComposer().getBackBufferSize() - 1;
            for (int i = 0; i < renderIterations; i++) {
                pixelRingBuffer.getPixelBuffer().clearBuffer();

                layerPipeline.forEach(layer -> {
                    layer.prepareBuffer();
                    layer.mergeBufferWith(pixelRingBuffer.getPixelBuffer(), 0, 0);
                    layer.disposeBuffer();
                });

                // int the last iteration we do not move pointer!
                if (i < (renderIterations - 1)) {
                    pixelRingBuffer.swap();
                }
            }
            isBackBufferEmpty = false;
        }
    }

    @Override
    public int compose() {
        final boolean isPipelineDirty = layerPipeline.stream()
                .map(Mergeable::isDirty)
                .reduce(true, (first, second) -> first && second);

        if (!isPipelineDirty) {
            return pixelRingBuffer.getCurrentBufferIndex();
        } else {
            pixelRingBuffer.swap();
            pixelRingBuffer.getPixelBuffer().clearBuffer();
            renderLayerBuffersInParallel(); // prepare buffers in parallel
            layerPipeline.forEach(layer -> {
                layer.mergeBufferWith(pixelRingBuffer.getPixelBuffer(), 0, 0);
                layer.disposeBuffer();
            });

            return pixelRingBuffer.getCurrentBufferIndex();
        }
    }

    private void renderLayerBuffersInParallel() {
        final CountDownLatch latch = new CountDownLatch(layerPipeline.size());
        for (final Mergeable layer : layerPipeline) {
            try {
                executorService.execute(layer::prepareBuffer);
            } catch (final Exception e) {
                log.error("{}: {}\n{}", getClass().getName(), e.getMessage(), e.getStackTrace());
            } finally {
                latch.countDown();
            }
        }

        try {
            latch.await();
        } catch (final InterruptedException e) {
            log.error("{}: {}\n{}", getClass().getName(), e.getMessage(), e.getStackTrace());
        }
    }

    @Override
    public PixelBuffer getPixelBuffer() {
        return pixelRingBuffer.getPixelBuffer();
    }

}
