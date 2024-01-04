package com.recom.commander.enginemodule;

import com.recom.commander.enginemodule.entity.RECOMEnvironment;
import com.recom.commander.enginemodule.entity.component.RECOMMapComponent;
import com.recom.tacview.engine.entity.Entity;
import com.recom.tacview.engine.entity.component.ComponentType;
import com.recom.tacview.engine.entity.component.MergeableLayerComponent;
import com.recom.tacview.engine.module.EngineModule;
import com.recom.tacview.engine.renderables.mergeable.BufferedMergeableWrapper;
import com.recom.tacview.engine.renderer.RenderProvider;
import com.recom.tacview.property.RendererProperties;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
public class TacViewerEngineModule extends EngineModule {

    @NonNull
    private final RendererProperties rendererProperties;
    @NonNull
    private final RenderProvider renderProvider;

    @NonNull
    private final RECOMMapComponent mapComponent;


    public TacViewerEngineModule(
            @NonNull final RECOMEnvironment world,
            @NonNull final RendererProperties rendererProperties,
            @NonNull final RenderProvider renderProvider,

            @NonNull final RECOMMapComponent mapComponent
    ) {
        super(world);
        this.rendererProperties = rendererProperties;
        this.renderProvider = renderProvider;

        this.mapComponent = mapComponent;
    }

    @Override
    public void init() {
        final Entity mapEntity = new Entity();

        final BufferedMergeableWrapper bufferedMergeableWrapper = new BufferedMergeableWrapper(mapComponent, renderProvider);
        final MergeableLayerComponent mergeableMapLayerComponent = new MergeableLayerComponent(bufferedMergeableWrapper);
        mergeableMapLayerComponent.setComponentType(ComponentType.MAP_LAYER);
        mapEntity.addComponent(mergeableMapLayerComponent);

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
