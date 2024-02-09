package com.recom.tacview.property;

import com.recom.commons.units.PixelDimension;
import com.recom.observer.BufferedSubject;
import com.recom.observer.Notification;
import jakarta.annotation.Nullable;
import jakarta.annotation.PostConstruct;
import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties("engine")
public class EngineProperties implements IsEngineProperties {

    @NonNull
    private final BufferedSubject<IsEngineProperties> bufferedSubject = new BufferedSubject<>();

    @Nullable
    private static PixelDimension singletonPixelDimension = null;
    private long tickThresholdNanoTime = 0;
    private long rendererFramesThresholdNanoTime = 0;

    private int rendererWidth;
    private int rendererHeight;
    private int rendererScale;
    private boolean parallelizedRendering;
    private int renderFragments;
    private int rendererThreadPoolSize;
    private int targetFps;
    private int composerBackBufferSize;
    private int targetTps;

    @PostConstruct
    public void postConstruct() {
        bufferedSubject.notifyObserversWith(Notification.of(this));
    }

    public long getTickThresholdNanoTime() {
        if (tickThresholdNanoTime == 0.0) {
            tickThresholdNanoTime = Duration.ofSeconds(1).toNanos() / targetTps;
        }

        return tickThresholdNanoTime;
    }

    @NonNull
    public PixelDimension toRendererDimension() {
        if (singletonPixelDimension == null) {
            singletonPixelDimension = PixelDimension.of(getRendererWidth(), getRendererHeight());
        }

        return singletonPixelDimension;
    }

    public long getFrameThresholdNanoTime() {
        if (rendererFramesThresholdNanoTime == 0.0) {
            rendererFramesThresholdNanoTime = Duration.ofSeconds(1).toNanos() / targetFps;
        }

        return rendererFramesThresholdNanoTime;
    }

    public int getScaledWindowWidth() {
        return rendererWidth * rendererScale;
    }

    public int getScaledWindowHeight() {
        return rendererHeight * rendererScale;
    }

}
