package com.recom.commander.property.user;

import com.recom.commander.RecomCommanderApplication;
import com.recom.commons.units.PixelDimension;
import com.recom.dynamicproperties.ObservableDynamicUserProperties;
import lombok.*;

import java.time.Duration;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EngineProperties extends ObservableDynamicUserProperties<EngineProperties> {

    /**
     * static singleton instances are used to cache the values of the properties, avoid recalculating them every time
     * static fields are ignored by the serialization process, so they are not persisted!
     */
    private static PixelDimension cachedSingletonPixelDimension = null;
    private static Long cachedFramesThresholdNanoTime = null;
    private static Long cachedTickThresholdNanoTime = null;


    @NonNull
    @Override
    public String getApplicationName() {
        return RecomCommanderApplication.APPLICATION_NAME;
    }

    @NonNull
    @Override
    public String getPropertyFileName() {
        return "engine";
    }


    @Builder.Default
    private Integer width = 800;
    @Builder.Default
    private Integer height = 600;
    @Builder.Default
    private Integer scale = 1;
    @Builder.Default
    private Integer targetFps = 66;
    @Builder.Default
    private Integer targetTps = 66;
    @Builder.Default
    private Boolean parallelizedRendering = true;
    @Builder.Default
    private Integer renderFragments = 1;
    @Builder.Default
    private Integer threadPoolSize = -1;
    @Builder.Default
    private Integer backBufferSize = 10;


    public int getScaledWindowWidth() {
        return width * scale;
    }

    public int getScaledWindowHeight() {
        return height * scale;
    }

    @NonNull
    public PixelDimension toRendererDimension() {
        if (EngineProperties.cachedSingletonPixelDimension == null) {
            EngineProperties.cachedSingletonPixelDimension = PixelDimension.of(getWidth(), getHeight());
        }

        return EngineProperties.cachedSingletonPixelDimension;
    }

    public long getFrameThresholdNanoTime() {
        if (EngineProperties.cachedFramesThresholdNanoTime == 0.0) {
            EngineProperties.cachedFramesThresholdNanoTime = Duration.ofSeconds(1).toNanos() / targetFps;
        }

        return cachedFramesThresholdNanoTime;
    }

    public long getTickThresholdNanoTime() {
        if (EngineProperties.cachedTickThresholdNanoTime == 0.0) {
            EngineProperties.cachedTickThresholdNanoTime = Duration.ofSeconds(1).toNanos() / targetTps;
        }

        return EngineProperties.cachedTickThresholdNanoTime;
    }

}
