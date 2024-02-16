package com.recom.tacview.engine.renderer;

import com.recom.tacview.engine.graphics.IsBufferable;
import com.recom.tacview.engine.graphics.IsScanable;
import com.recom.tacview.property.IsEngineProperties;
import com.recom.tacview.service.RendererExecutorProvider;
import com.recom.tacview.service.argb.ARGBCalculatorProvider;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

@Slf4j
@Component
class MultithreadedSoftwareRenderer3 extends RendererTemplate {

    @NonNull
    private final ExecutorService rendererExecutorService;
    @NonNull
    private final IsEngineProperties engineProperties;


    MultithreadedSoftwareRenderer3(
            @NonNull final RendererExecutorProvider rendererExecutorProvider,
            @NonNull final ARGBCalculatorProvider argbCalculatorProvider,
            @NonNull final IsEngineProperties engineProperties
    ) {
        super(argbCalculatorProvider);
        this.engineProperties = engineProperties;
        this.rendererExecutorService = rendererExecutorProvider.provideNewExecutor();
    }

    @Override
    public void render(
            @NonNull final IsScanable source,
            @NonNull final IsBufferable target,
            final int xOffset,
            final int yOffset
    ) {
        final int parallelSegments = engineProperties.getRenderFragments();

        final int sourceWidth = source.getDimension().getWidthX();
        final int sourceHeight = source.getDimension().getHeightY();
        final int targetWidth = target.getDimension().getWidthX();
        final int targetHeight = target.getDimension().getHeightY();

        // Calculate boundaries of relevant area in advance
        final int startX = Math.max(0, -xOffset);
        final int endX = Math.min(sourceWidth, targetWidth - xOffset);
        final int startY = Math.max(0, -yOffset);
        final int endY = Math.min(sourceHeight, targetHeight - yOffset);

        final int segmentHeight = (int) Math.ceil((endY - startY) / (float) parallelSegments);

        final CountDownLatch latch = new CountDownLatch(parallelSegments);
        for (int segment = 0; segment < parallelSegments; segment++) {
            final int startRow = startY + segment * segmentHeight;
            final int endRow = Math.min(startRow + segmentHeight, endY);

            rendererExecutorService.execute(() -> {
                try {
                    for (int y = startRow; y < endRow; y++) {
                        final int copyToY = y + yOffset;

                        for (int x = startX; x < endX; x++) {
                            final int copyToX = x + xOffset;

                            final int colorValue = source.scanPixelAt(x, y);
                            if (colorValue == 0xFFff00ff) continue;
                            final int alphaComponent = argbCalculatorProvider.provide().getAlphaComponent(colorValue);
                            if (alphaComponent < 0xFF) {
                                // set color blending (alpha channel)
                                target.bufferPixelAt(copyToX, copyToY, argbCalculatorProvider.provide().blend(colorValue, target.scanPixelAt(copyToX, copyToY)));
                            } else {
                                // set color without blending
                                target.bufferPixelAt(copyToX, copyToY, colorValue);
                            }
                        }
                    }
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

}
