package com.recom.tacview.engine.graphics;

import com.recom.tacview.engine.entity.Environment;
import com.recom.tacview.engine.entity.component.ComponentType;
import com.recom.tacview.engine.entity.component.RenderableComponent;
import com.recom.tacview.engine.graphics.buffer.PixelBuffer;
import com.recom.tacview.engine.graphics.buffer.PixelRingBuffer;
import com.recom.tacview.engine.renderables.Mergeable;
import com.recom.tacview.engine.renderables.mergeable.MergeableComponentLayer;
import com.recom.tacview.engine.renderer.RenderProvider;
import com.recom.tacview.property.RendererProperties;
import com.recom.tacview.service.RendererExecutorProvider;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

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
        // @TODO: refactor this to use cached entity/component lists (cached by environment -> implement component locator)
        /*
         final Map<ComponentProcessingOrder, List<RenderableComponent>> componentsByLayer = environment.getEntities().stream()
                .flatMap(entity -> entity.locateComponents(RenderableComponent.class).stream())
                .collect(Collectors.groupingBy(HasComponentType::getComponentProcessingOrder));

        // cached list of layers to render -> create new, only if entity/component list has changed!
        final List<MergeableComponentLayer> layerPipeline = componentsByLayer.entrySet().stream()
                .map(entrySet -> new MergeableComponentLayer(rendererProperties, renderProvider, entrySet.getKey(), entrySet.getValue()))
                .toList();
         */

        final Map<Integer, List<RenderableComponent>> componentsByLayer = environment.getEntities().stream()
                .flatMap(entity -> entity.locateComponents(ComponentType.RenderableComponent).stream())
                .collect(Collectors.groupingBy(RenderableComponent::getZIndex));
//                .collect(Collectors.groupingBy(x->x.getComponentProcessingOrder()));

        // cached list of layers to render -> create new, only if entity/component list has changed!
        final List<MergeableComponentLayer> layerPipeline = componentsByLayer.entrySet().stream()
                .map(entrySet -> new MergeableComponentLayer(rendererProperties, renderProvider, entrySet.getKey(), entrySet.getValue()))
                .toList();

        if (!isPipelineDirty(layerPipeline)) {
            return pixelRingBuffer.getCurrentBufferIndex();
        } else {
            pixelRingBuffer.next();
            pixelRingBuffer.getPixelBuffer().clearBuffer();
            renderLayerPipelineInParallel(layerPipeline);
            layerPipeline.forEach(layer -> {
                layer.mergeBufferWith(pixelRingBuffer.getPixelBuffer(), 0, 0);
                layer.dispose();
            });

            return pixelRingBuffer.getCurrentBufferIndex();
        }
    }

    private boolean isPipelineDirty(@NonNull final List<MergeableComponentLayer> layerPipeline) {
        return layerPipeline.stream()
                .map(Mergeable::isDirty)
                .reduce(true, (first, second) -> first && second);
    }

    private void renderLayerPipelineInParallel(@NonNull final List<MergeableComponentLayer> layerPipeline) {
        final CountDownLatch latch = new CountDownLatch(layerPipeline.size());
        for (final MergeableComponentLayer mergeableLayer : layerPipeline) {
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
