package com.recom.commander.enginemodule;

import com.recom.commander.enginemodule.entity.RECOMEnvironment;
import com.recom.tacview.engine.graphics.ScreenComposer;
import com.recom.tacview.engine.module.EngineModule;
import com.recom.tacview.engine.renderables.mergeable.SolidScreenMergeable;
import com.recom.tacview.engine.renderer.RenderProvider;
import com.recom.tacview.property.RendererProperties;
import com.recom.tacview.service.RandomProvider;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
public class TacViewerEngineModule extends EngineModule {

    @NonNull
    private final RendererProperties rendererProperties;
    @NonNull
    private final ScreenComposer screenComposer;
    @NonNull
    private final RenderProvider renderProvider;
//    @NonNull
//    private final RandomProvider randomProvider;


    public TacViewerEngineModule(
            @NonNull final RECOMEnvironment world,
            @NonNull final RendererProperties rendererProperties,
            @NonNull final ScreenComposer screenComposer,
            @NonNull final RenderProvider renderProvider,
            @NonNull final RandomProvider randomProvider
    ) {
        super(world);
        this.rendererProperties = rendererProperties;
        this.screenComposer = screenComposer;
        this.renderProvider = renderProvider;

//        this.randomProvider = randomProvider;
    }

    @Override
    public void init() {
        final SolidScreenMergeable greenScreenMergeable = new SolidScreenMergeable(rendererProperties, renderProvider, 0xFF54B262);
//        final ScanableNoiseMergeable greenScreenMergeable = new ScanableNoiseMergeable(renderProvider, rendererProperties.toRendererDimension(), randomProvider);
        screenComposer.getLayerPipeline().clear();
        screenComposer.getLayerPipeline().add(greenScreenMergeable);
    }

    @Override
    public void startEngineModule() {

    }

    @Override
    public void update(final long elapsedNanoTime) {
        super.update(elapsedNanoTime);
    }

    @Override
    public void handleInput() {
        super.handleInput();
    }

}
