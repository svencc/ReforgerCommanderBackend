package com.recom.tacview.engine.graphics;

import com.recom.commons.units.ResizeCommand;
import com.recom.tacview.engine.ecs.environment.Environment;
import com.recom.tacview.engine.graphics.buffer.PixelBuffer;
import com.recom.tacview.engine.graphics.buffer.PixelRingBuffer;
import com.recom.tacview.engine.graphics.renderpipeline.IsRenderPipeline;
import com.recom.tacview.engine.renderables.mergeable.MergeableComponentLayer;
import com.recom.tacview.property.IsEngineProperties;
import com.recom.tacview.service.RendererExecutorProvider;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

@Slf4j
@Component
public class ScreenComposer implements IsComposable {

    @NonNull
    private final ExecutorService renderExecutorService;
    @NonNull
    private final PixelRingBuffer pixelRingBuffer;


    public ScreenComposer(
            @NonNull final IsEngineProperties engineProperties,
            @NonNull final RendererExecutorProvider rendererExecutorProvider
    ) {
        this.renderExecutorService = rendererExecutorProvider.provideNewExecutor();
        this.pixelRingBuffer = new PixelRingBuffer(engineProperties.toRendererDimension(), engineProperties.getComposerBackBufferSize());
    }

    @Override
    public void compose(@NonNull final Environment environment) {
        if (environment.getRenderPipeline().isDirty()) {
            pixelRingBuffer.getPixelBuffer().clearBuffer();
            renderLayersInParallel(environment.getRenderPipeline());
            environment.getRenderPipeline().getLayers().forEach(layer -> {
                layer.mergeBufferWith(pixelRingBuffer.getPixelBuffer(), 0, 0);
                layer.dispose();
            });
            pixelRingBuffer.next();

            environment.getRenderPipeline().propagateCleanStateToChildren();
        }
    }

    private void renderLayersInParallel(@NonNull final IsRenderPipeline renderPipeline) {
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

    @NonNull
    public PixelBuffer getPreviouslyFinishedPixelBuffer() {
        return pixelRingBuffer.getPreviouslyFinishedPixelBuffer();
    }

    @NonNull
    public int getPreviouslyFinishedBufferIndex() {
        return pixelRingBuffer.getPreviouslyFinishedBufferIndex();
    }

    public void resizeBuffer(@NonNull final ResizeCommand resizeCommand) {
        pixelRingBuffer.resizeBuffer(resizeCommand);
    }

}
