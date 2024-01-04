package com.recom.commander.enginemodule;

import com.recom.commander.enginemodule.entity.RECOMEnvironment;
import com.recom.tacview.engine.entity.Entity;
import com.recom.tacview.engine.entity.component.MergeableLayerComponent;
import com.recom.tacview.engine.graphics.ScreenComposer;
import com.recom.tacview.engine.module.EngineModule;
import com.recom.tacview.engine.renderables.mergeable.SolidColorMergeable;
import com.recom.tacview.engine.renderer.RenderProvider;
import com.recom.tacview.property.RendererProperties;
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
        final Entity mapEntity = new Entity();
        final SolidColorMergeable greenScreenMergeable = new SolidColorMergeable(rendererProperties, renderProvider, 0xFF54B262);
        final MergeableLayerComponent mergeableLayerComponent = new MergeableLayerComponent(greenScreenMergeable);
        mapEntity.addComponent(mergeableLayerComponent);

        getEnvironment().getEntities().add(mapEntity);
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
