package com.recom.tacview.engine.graphics;

import com.recom.tacview.engine.entity.Environment;
import com.recom.tacview.engine.graphics.buffer.PixelBuffer;
import com.recom.tacview.engine.graphics.buffer.PixelRingBuffer;
import com.recom.tacview.engine.renderables.mergeable.MergeableComponentLayer;
import com.recom.tacview.engine.renderer.RenderProvider;
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
    private final RendererProperties rendererProperties;
    @NonNull
    private final RenderProvider renderProvider;
    @NonNull
    private final ExecutorService executorService;
    @NonNull
    private final PixelRingBuffer pixelRingBuffer;


    public ScreenComposer(
            @NonNull final RendererProperties rendererProperties,
            @NonNull final RenderProvider renderProvider,
            @NonNull final RendererExecutorProvider rendererExecutorProvider
    ) {
        this.rendererProperties = rendererProperties;
        this.renderProvider = renderProvider;
        this.executorService = rendererExecutorProvider.provideNewExecutor();
        this.pixelRingBuffer = new PixelRingBuffer(rendererProperties.toRendererDimension(), rendererProperties.getComposer().getBackBufferSize());
    }

    @Override
    public int compose(@NonNull final Environment environment) {
        final RenderPipeline renderPipeline = environment.getRenderPipeline(rendererProperties, renderProvider);

        if (!renderPipeline.isDirty()) {
            return pixelRingBuffer.getCurrentBufferIndex();
        } else {
            pixelRingBuffer.next();
            pixelRingBuffer.getPixelBuffer().clearBuffer();
            renderLayerPipelineInParallel(renderPipeline);
            renderPipeline.getLayers().forEach(layer -> {
                layer.mergeBufferWith(pixelRingBuffer.getPixelBuffer(), 0, 0);
                layer.dispose();
            });

            return pixelRingBuffer.getCurrentBufferIndex();
        }
    }

    private void renderLayerPipelineInParallel(@NonNull final RenderPipeline renderPipeline) {
        final CountDownLatch latch = new CountDownLatch(renderPipeline.getLayers().size());
        for (final MergeableComponentLayer mergeableLayer : renderPipeline.getLayers()) {
            try {
                executorService.execute(mergeableLayer::prepareBuffer);
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

    @NonNull
    @Override
    public PixelBuffer getPixelBuffer() {
        return pixelRingBuffer.getPixelBuffer();
    }

}
