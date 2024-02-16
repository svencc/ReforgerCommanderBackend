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
class MultithreadedSoftwareRenderer2 extends RendererTemplate {

    @NonNull
    private final ExecutorService rendererExecutorService;
    @NonNull
    private final IsEngineProperties engineProperties;


    MultithreadedSoftwareRenderer2(
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
        final int parallelSegments = 1;

        int sourceHeight = source.getDimension().getHeightY();
        int segmentHeight = (int) Math.ceil(source.getDimension().getHeightY() / (float) parallelSegments);

        final CountDownLatch latch = new CountDownLatch(parallelSegments);
        for (int segment = 0; segment < parallelSegments; segment++) {
            final int startRow = segment * segmentHeight;
            final int endRow = Math.min(startRow + segmentHeight, sourceHeight);

            rendererExecutorService.execute(() -> {
                try {
                    for (int y = startRow; y < endRow; y++) {
                        final int copyToY = y + yOffset;
                        if (copyToY < 0 || copyToY >= target.getDimension().getHeightY()) continue;

                        for (int x = 0; x < source.getDimension().getWidthX(); x++) {
                            final int copyToX = x + xOffset;
                            if (copyToX < 0 || copyToX >= target.getDimension().getWidthX()) continue;

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
