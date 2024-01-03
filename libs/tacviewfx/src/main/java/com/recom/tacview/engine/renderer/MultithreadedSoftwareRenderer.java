package com.recom.tacview.engine.renderer;

import com.recom.tacview.engine.graphics.Bufferable;
import com.recom.tacview.engine.graphics.Scanable;
import com.recom.tacview.service.RendererExecutorProvider;
import com.recom.tacview.service.argb.ARGBCalculatorProvider;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

@Slf4j
@Component
class MultithreadedSoftwareRenderer extends RendererTemplate {

    @NonNull
    private final ExecutorService executorService;


    MultithreadedSoftwareRenderer(
            @NonNull final RendererExecutorProvider rendererExecutorProvider,
            @NonNull final ARGBCalculatorProvider argbCalculatorProvider
    ) {
        super(argbCalculatorProvider);
        this.executorService = rendererExecutorProvider.provideNewExecutor();
    }

    @Override
    public void render(
            @NonNull final Scanable source,
            @NonNull final Bufferable target,
            final int xOffset,
            final int yOffset
    ) {
        final CountDownLatch latch = new CountDownLatch(source.getDimension().getHeightY());
        for (int y = 0; y < source.getDimension().getHeightY(); y++) {
            final int copyToY = y + yOffset;
            if (copyToY < 0 || copyToY >= target.getDimension().getHeightY()) {
                latch.countDown();
                continue;
            }

            final int yFinal = y;
            try {
                for (int x = 0; x < source.getDimension().getWidthX(); x++) {
                    final int copyToX = x + xOffset;
                    if (copyToX < 0 || copyToX >= target.getDimension().getWidthX()) continue;

                    final int colorValue = source.scanPixelAt(x, yFinal);
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

}
