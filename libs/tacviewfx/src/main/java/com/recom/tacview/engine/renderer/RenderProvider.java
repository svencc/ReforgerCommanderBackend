package com.recom.tacview.engine.renderer;

import com.recom.tacview.engine.graphics.Renderable;
import com.recom.tacview.property.RendererProperties;
import com.recom.tacview.service.RendererExecutorProvider;
import com.recom.tacview.service.argb.ARGBCalculatorProvider;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class RenderProvider {

    @NonNull
    private final RendererProperties rendererProperties;
    @NonNull
    private final RendererExecutorProvider rendererExecutorProvider;
    @NonNull
    private final ARGBCalculatorProvider argbCalculatorProvider;
    @Nullable
    private Renderable instance;

    public Renderable provide() {
        if (instance == null) {
            if (rendererProperties.isParallelizedRendering()) {
                instance = new MultithreadedSoftwareRenderer(rendererExecutorProvider, argbCalculatorProvider);
            } else {
                instance = new SoftwareRenderer(argbCalculatorProvider);
            }
        }

        return instance;
    }

}
