package com.recom.tacview.engine.graphics;

import com.recom.tacview.engine.entity.component.MergeableLayerComponent;
import com.recom.tacview.engine.entity.environment.EnvironmentBase;
import com.recom.tacview.engine.graphics.buffer.PixelBuffer;
import com.recom.tacview.engine.graphics.buffer.PixelRingBuffer;
import com.recom.tacview.engine.renderables.Mergeable;
import com.recom.tacview.property.RendererProperties;
import com.recom.tacview.service.RendererExecutorProvider;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

@Slf4j
@Component
public class ScreenComposer implements Composable {

    @NonNull
    private final ExecutorService executorService;
    @NonNull
    private final PixelRingBuffer pixelRingBuffer;


    public ScreenComposer(
            @NonNull final RendererProperties rendererProperties,
            @NonNull final RendererExecutorProvider rendererExecutorProvider
    ) {
        this.executorService = rendererExecutorProvider.provideNewExecutor();
        this.pixelRingBuffer = new PixelRingBuffer(rendererProperties.toRendererDimension(), rendererProperties.getComposer().getBackBufferSize());
    }

    @Override
    public int compose(@NonNull final EnvironmentBase environment) {
        final List<MergeableLayerComponent> layerPipeline = environment.getEntities().stream()
                .flatMap(entity -> entity.locateComponents(MergeableLayerComponent.class).stream())
                .toList();

        final boolean isPipelineDirty = layerPipeline.stream()
                .map(MergeableLayerComponent::getMergeable)
                .map(Mergeable::isDirty)
                .reduce(true, (first, second) -> first && second);

        if (!isPipelineDirty) {
            return pixelRingBuffer.getCurrentBufferIndex();
        } else {
            pixelRingBuffer.next();
            pixelRingBuffer.getPixelBuffer().clearBuffer();
            renderLayerPipelineInParallel(layerPipeline);
            layerPipeline.forEach(layer -> {
                layer.getMergeable().mergeBufferWith(pixelRingBuffer.getPixelBuffer(), 0, 0);
                layer.getMergeable().dispose();
            });

            return pixelRingBuffer.getCurrentBufferIndex();
        }
    }

    private void renderLayerPipelineInParallel(@NonNull final List<MergeableLayerComponent> layerPipeline) {
        final CountDownLatch latch = new CountDownLatch(layerPipeline.size());
        for (final MergeableLayerComponent layerComponent : layerPipeline) {
            try {
                final Mergeable mergeableLayer = layerComponent.getMergeable();
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

    @Override
    public PixelBuffer getPixelBuffer() {
        return pixelRingBuffer.getPixelBuffer();
    }

}
