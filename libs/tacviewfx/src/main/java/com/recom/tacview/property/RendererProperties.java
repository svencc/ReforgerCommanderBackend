package com.recom.tacview.property;

import com.recom.tacview.engine.units.PixelDimension;
import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties("engine.renderer")
public class RendererProperties {

    private static PixelDimension singletonPixelDimension = null;
    private int width;
    private int height;
    private boolean parallelizedRendering;
    private int threadPoolSize;
    private int targetFps;
    private long framesThresholdNanoTime = 0;
    private ComposerProperties composer;

    @NonNull
    public PixelDimension toRendererDimension() {
        if (singletonPixelDimension == null) {
            singletonPixelDimension = PixelDimension.of(getWidth(), getHeight());
        }

        return singletonPixelDimension;
    }

    public long getFrameThresholdNanoTime() {
        if (framesThresholdNanoTime == 0.0) {
            framesThresholdNanoTime = Duration.ofSeconds(1).toNanos() / targetFps;
        }

        return framesThresholdNanoTime;
    }

}
