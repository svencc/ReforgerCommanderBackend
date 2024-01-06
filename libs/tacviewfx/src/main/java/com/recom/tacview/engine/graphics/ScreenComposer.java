package com.recom.tacview.engine.graphics;

import com.recom.tacview.engine.entitycomponentsystem.environment.Environment;
import com.recom.tacview.engine.graphics.buffer.PixelBuffer;
import com.recom.tacview.engine.graphics.buffer.PixelRingBuffer;
import com.recom.tacview.engine.graphics.renderpipeline.IsRenderPipeline;
import com.recom.tacview.engine.renderables.mergeable.MergeableComponentLayer;
import com.recom.tacview.property.RendererProperties;
import com.recom.tacview.service.RendererExecutorProvider;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

@Slf4j
@Component
public class ScreenComposer implements Composable {

    @NonNull
    private final ExecutorService renderExecutorService;
    @NonNull
    private final PixelRingBuffer pixelRingBuffer;


    public ScreenComposer(
            @NonNull final RendererProperties rendererProperties,
            @NonNull final RendererExecutorProvider rendererExecutorProvider
    ) {
        this.renderExecutorService = rendererExecutorProvider.provideNewExecutor();
        this.pixelRingBuffer = new PixelRingBuffer(rendererProperties.toRendererDimension(), rendererProperties.getComposer().getBackBufferSize());
    }

    @Override
    public int compose(@NonNull final Environment environment) {
        if (!environment.getRenderPipeline().isDirty()) {
            return pixelRingBuffer.getCurrentBufferIndex();
        } else {
            pixelRingBuffer.next();
            pixelRingBuffer.getPixelBuffer().clearBuffer();
            renderLayerPipelineInParallel(environment.getRenderPipeline());
            environment.getRenderPipeline().getLayers().forEach(layer -> {
                layer.mergeBufferWith(pixelRingBuffer.getPixelBuffer(), 0, 0);
                layer.dispose();
            });

            environment.getRenderPipeline().propagateCleanStateToChildren();

            return pixelRingBuffer.getCurrentBufferIndex();
        }
    }

    private void renderLayerPipelineInParallel(@NonNull final IsRenderPipeline renderPipeline) {
        final CountDownLatch latch = new CountDownLatch(renderPipeline.getLayers().size());
        for (final MergeableComponentLayer mergeableLayer : renderPipeline.getLayers()) {
            renderExecutorService.execute(() -> {
                try {
                    if (mergeableLayer.isDirty()) {
                        mergeableLayer.prepareBuffer();
                    }
                } catch (final Exception e) {
                    log.error("{}: {}\n{}", getClass().getName(), e.getMessage(), e.getStackTrace());
                } finally {
                    latch.countDown();
                }
            });
        }

        try {
            latch.await();
        } catch (final InterruptedException e) {
            log.error("{}: {}\n{}", getClass().getName(), e.getMessage(), e.getStackTrace());
        }
    }

    @NonNull
    @Override
    public PixelBuffer getPixelBuffer() {
        return pixelRingBuffer.getPixelBuffer();
    }

}
