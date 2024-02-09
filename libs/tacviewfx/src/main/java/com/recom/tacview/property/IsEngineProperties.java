package com.recom.tacview.property;

import com.recom.commons.units.PixelDimension;
import com.recom.observer.HasBufferedSubject;
import com.recom.observer.HasSubject;
import lombok.NonNull;

public interface IsEngineProperties extends HasBufferedSubject<IsEngineProperties> {

    int getRendererWidth();

    int getRendererHeight();

    int getRendererScale();

    boolean isParallelizedRendering();

    int getRenderFragments();

    int getRendererThreadPoolSize();

    int getTargetFps();

    int getComposerBackBufferSize();

    int getTargetTps();

    long getTickThresholdNanoTime();

    @NonNull PixelDimension toRendererDimension();

    long getFrameThresholdNanoTime();

    int getScaledWindowWidth();

    int getScaledWindowHeight();

}
