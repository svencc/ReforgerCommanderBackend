package com.recom.tacview.engine.renderer;

import com.recom.tacview.engine.graphics.IsRenderable;
import com.recom.tacview.property.EngineProperties;
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
    private final EngineProperties engineProperties;
    @NonNull
    private final RendererExecutorProvider rendererExecutorProvider;
    @NonNull
    private final ARGBCalculatorProvider argbCalculatorProvider;
    @Nullable
    private IsRenderable instance;


    public IsRenderable provide() {
        if (instance == null) {
            if (engineProperties.isParallelizedRendering()) {
                instance = new MultithreadedSoftwareRenderer3(rendererExecutorProvider, argbCalculatorProvider, engineProperties);
            } else {
                instance = new SoftwareRenderer(argbCalculatorProvider);
            }
        }

        return instance;
    }

}
