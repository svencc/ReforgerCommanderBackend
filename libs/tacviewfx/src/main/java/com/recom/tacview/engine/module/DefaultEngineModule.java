package com.recom.tacview.engine.module;

import com.recom.tacview.engine.components.mergeable.ScanableNoiseMergeable;
import com.recom.tacview.engine.entity.World;
import com.recom.tacview.engine.graphics.ScreenComposer;
import com.recom.tacview.engine.renderer.RenderProvider;
import com.recom.tacview.property.RendererProperties;
import com.recom.tacview.property.TickProperties;
import com.recom.tacview.service.RandomProvider;
import lombok.NonNull;

public class DefaultEngineModule extends EngineModuleTemplate {

    @NonNull
    private final RendererProperties rendererProperties;
    @NonNull
    private final TickProperties tickProperties;
    @NonNull
    private final ScreenComposer screenComposer;
    @NonNull
    private final RandomProvider randomProvider;
    @NonNull
    private final RenderProvider renderProvider;

    public DefaultEngineModule(
            @NonNull final World world,
            @NonNull final RendererProperties rendererProperties,
            @NonNull final TickProperties tickProperties,
            @NonNull final ScreenComposer screenComposer,
            @NonNull final RandomProvider randomProvider,
            @NonNull final RenderProvider renderProvider
    ) {
        super(world);
        this.rendererProperties = rendererProperties;
        this.tickProperties = tickProperties;
        this.screenComposer = screenComposer;
        this.randomProvider = randomProvider;
        this.renderProvider = renderProvider;
    }


    @Override
    public void init() {
        final ScanableNoiseMergeable scanableNoiseLayer = new ScanableNoiseMergeable(renderProvider, rendererProperties.toRendererDimension(), randomProvider);
        screenComposer.getLayerPipeline().clear();
        screenComposer.getLayerPipeline().add(scanableNoiseLayer);
    }

    @Override
    public void startEngineModule() {

    }

    @Override
    public void update() {
        super.update();
    }

    @Override
    public void handleInput() {
        super.handleInput();
    }

}
