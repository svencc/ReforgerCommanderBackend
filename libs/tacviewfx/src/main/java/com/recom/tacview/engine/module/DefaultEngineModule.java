package com.recom.tacview.engine.module;

import com.recom.tacview.engine.renderer.RenderProvider;
import com.recom.tacview.engine.components.mergeable.ScanableNoiseMergeable;
import com.recom.tacview.engine.graphics.ScreenComposer;
import com.recom.tacview.property.RendererProperties;
import com.recom.tacview.service.RandomProvider;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultEngineModule extends EngineModuleTemplate {

    @NonNull
    private final RendererProperties rendererProperties;
    @NonNull
    private final ScreenComposer screenComposer;
    @NonNull
    private final RandomProvider randomProvider;
    @NonNull
    private final RenderProvider renderProvider;

    public void init() {
        final ScanableNoiseMergeable scanableNoiseLayer = new ScanableNoiseMergeable(renderProvider, rendererProperties.toRendererDimension(), randomProvider);
        screenComposer.getLayerPipeline().clear();
        screenComposer.getLayerPipeline().add(scanableNoiseLayer);
    }

    public void startEngineModule() {

    }

}
