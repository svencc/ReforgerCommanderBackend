package com.recom.commander.property.user;

import com.recom.commander.RecomCommanderApplication;
import com.recom.commons.units.PixelDimension;
import com.recom.dynamicproperties.ObservableDynamicUserProperties;
import com.recom.observer.BufferedSubject;
import com.recom.observer.Notification;
import com.recom.tacview.property.IsEngineProperties;
import jakarta.annotation.Nullable;
import lombok.*;

import java.time.Duration;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DynamicEngineProperties extends ObservableDynamicUserProperties<DynamicEngineProperties> implements IsEngineProperties {


    /**
     * static singleton instances are used to cache the values of the properties, avoid recalculating them every time
     * static fields are ignored by the serialization process, so they are not persisted!
     */
    private static PixelDimension cachedSingletonPixelDimension = null;
    private static Long cachedFramesThresholdNanoTime = null;
    private static Long cachedTickThresholdNanoTime = null;
    @Nullable
    private BufferedSubject<IsEngineProperties> bufferedSubject = null;

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
    private Integer rendererWidth = 800;
    @Builder.Default
    private Integer rendererHeight = 600;
    @Builder.Default
    private Integer rendererScale = 1;
    @Builder.Default
    private Boolean parallelizedRendering = true;
    @Builder.Default
    private Integer renderFragments = 1;
    @Builder.Default
    private Integer rendererThreadPoolSize = -1;
    @Builder.Default
    private Integer targetFps = 66;
    @Builder.Default
    private Integer composerBackBufferSize = 10;
    @Builder.Default
    private Integer targetTps = 66;

    public void setRendererWidth(final int rendererWidth) {
        this.rendererWidth = Math.max(320, rendererWidth);
        DynamicEngineProperties.cachedSingletonPixelDimension = null;
    }

    public void setRendererHeight(final int rendererHeight) {
        this.rendererHeight = Math.max(240, rendererHeight);
        DynamicEngineProperties.cachedSingletonPixelDimension = null;
    }

    public int getRendererWidth() {
        return Math.max(320, rendererWidth);
    }

    public int getRendererHeight() {
        return Math.max(240, rendererHeight);
    }

    public int getRendererScale() {
        return rendererScale;
    }

    public boolean isParallelizedRendering() {
        return parallelizedRendering;
    }

    public int getRenderFragments() {
        return renderFragments;
    }

    public int getRendererThreadPoolSize() {
        return rendererThreadPoolSize;
    }

    public int getTargetFps() {
        return targetFps;
    }

    public int getComposerBackBufferSize() {
        return composerBackBufferSize;
    }

    public int getTargetTps() {
        return targetTps;
    }

    public int getScaledWindowWidth() {
        return rendererWidth * rendererScale;
    }

    public int getScaledWindowHeight() {
        return rendererHeight * rendererScale;
    }

    @NonNull
    public PixelDimension toRendererDimension() {
        if (DynamicEngineProperties.cachedSingletonPixelDimension == null) {
            DynamicEngineProperties.cachedSingletonPixelDimension = PixelDimension.of(rendererWidth, rendererHeight);
        }

        return DynamicEngineProperties.cachedSingletonPixelDimension;
    }

    public long getFrameThresholdNanoTime() {
        if (DynamicEngineProperties.cachedFramesThresholdNanoTime == null) {
            DynamicEngineProperties.cachedFramesThresholdNanoTime = Duration.ofSeconds(1).toNanos() / targetFps;
        }

        return cachedFramesThresholdNanoTime;
    }

    public long getTickThresholdNanoTime() {
        if (DynamicEngineProperties.cachedTickThresholdNanoTime == null) {
            DynamicEngineProperties.cachedTickThresholdNanoTime = Duration.ofSeconds(1).toNanos() / targetTps;
        }

        return DynamicEngineProperties.cachedTickThresholdNanoTime;
    }

    @NonNull
    @Override
    public BufferedSubject<IsEngineProperties> getBufferedSubject() {
        return provideBufferedSubject();
    }

    public void persist() {
        super.persist();
        propagateChanges();
    }

    @NonNull
    private BufferedSubject<IsEngineProperties> provideBufferedSubject() {
        if (bufferedSubject == null) {
            bufferedSubject = new BufferedSubject<>();
            bufferedSubject.notifyObserversWith(Notification.of(this)); // initialisation with first value
        }

        return bufferedSubject;
    }

    private void propagateChanges() {
        if (bufferedSubject != null) {
            provideBufferedSubject().notifyObserversWith(Notification.of(this));
        }
    }

    @Override
    public String toString() {
        return "DynamicEngineProperties";
    }

}
