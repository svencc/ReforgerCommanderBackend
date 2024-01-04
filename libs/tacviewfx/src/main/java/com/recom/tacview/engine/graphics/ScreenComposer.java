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
    private final ExecutorService executorService;
    @NonNull
    private final PixelRingBuffer pixelRingBuffer;
    @Getter
    @NonNull
    private final LinkedList<Mergeable> layerPipeline = new LinkedList<>();


    public ScreenComposer(
            @NonNull final RendererProperties rendererProperties,
            @NonNull final RendererExecutorProvider rendererExecutorProvider
    ) {
        this.executorService = rendererExecutorProvider.provideNewExecutor();
        this.pixelRingBuffer = new PixelRingBuffer(rendererProperties.toRendererDimension(), rendererProperties.getComposer().getBackBufferSize());
    }

    // layer sind mergeables
    // ein layer/mergeables muss durch die entities/renderables laufen und den layer fertigrendern
    // ich brauche ein template für mergeables ->  ein layer der sich die entsprechenden entities/components filtert
    @Override
    public int compose() {
        final boolean isPipelineDirty = layerPipeline.stream()
                .map(Mergeable::isDirty)
                .reduce(true, (first, second) -> first && second);

        if (!isPipelineDirty) {
            return pixelRingBuffer.getCurrentBufferIndex();
        } else {
            pixelRingBuffer.next();
            pixelRingBuffer.getPixelBuffer().clearBuffer();
            renderLayerBuffersInParallel(); // prepare buffers in parallel
            layerPipeline.forEach(layer -> {
                layer.mergeBufferWith(pixelRingBuffer.getPixelBuffer(), 0, 0);
                layer.dispose();
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
