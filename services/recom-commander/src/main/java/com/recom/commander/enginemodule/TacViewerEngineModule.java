package com.recom.commander.enginemodule;

import com.recom.commander.enginemodule.entity.RECOMEnvironment;
import com.recom.tacview.engine.graphics.ScreenComposer;
import com.recom.tacview.engine.module.EngineModuleTemplate;
import com.recom.tacview.engine.renderer.RenderProvider;
import com.recom.tacview.property.RendererProperties;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
public class TacViewerEngineModule extends EngineModuleTemplate {

    @NonNull
    private final RendererProperties rendererProperties;
    @NonNull
    private final ScreenComposer screenComposer;
    @NonNull
    private final RenderProvider renderProvider;


    public TacViewerEngineModule(
            @NonNull final RECOMEnvironment world,
            @NonNull final RendererProperties rendererProperties,
            @NonNull final ScreenComposer screenComposer,
            @NonNull final RenderProvider renderProvider
    ) {
        super(world);
        this.rendererProperties = rendererProperties;
        this.screenComposer = screenComposer;
        this.renderProvider = renderProvider;
    }

    @Override
    public void init() {
        final SolidScreenMergeable greenScreenMergeable = new SolidScreenMergeable(rendererProperties, renderProvider, 0xFF54B262);
        screenComposer.getLayerPipeline().clear();
        screenComposer.getLayerPipeline().add(greenScreenMergeable);
    }

    @Override
    public void startEngineModule() {

    }

    @Override
    public void update(final double elapsedNanoTime) {
        super.update(elapsedNanoTime);
    }

    @Override
    public void handleInput() {
        super.handleInput();
    }

}
