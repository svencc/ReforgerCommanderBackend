package com.recom.commander.enginemodule;

import com.recom.commander.enginemodule.entity.component.RECOMMapComponent;
import com.recom.tacview.engine.entity.Entity;
import com.recom.tacview.engine.entity.Environment;
import com.recom.tacview.engine.entity.component.ComponentType;
import com.recom.tacview.engine.entity.component.refactor.MergeableLayerComponent;
import com.recom.tacview.engine.module.EngineModule;
import com.recom.tacview.engine.renderables.mergeable.BufferedMergeableWrapper;
import com.recom.tacview.engine.renderer.RenderProvider;
import com.recom.tacview.property.RendererProperties;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
public class TacViewerEngineModule extends EngineModule {

    @NonNull
    private final RenderProvider renderProvider;
//    @NonNull
//    private final RECOMMapComponent mapComponent;


    public TacViewerEngineModule(
            @NonNull final Environment environment,
            @NonNull final RendererProperties rendererProperties,
            @NonNull final RenderProvider renderProvider
    ) {
        super(environment);
        this.renderProvider = renderProvider;

//        this.mapComponent = mapComponent;
    }

    @Override
    public void init() {
//        final Entity mapEntity = new Entity();
//
//        final BufferedMergeableWrapper bufferedMergeableWrapper = new BufferedMergeableWrapper(mapComponent, renderProvider);
//        final MergeableLayerComponent mergeableMapLayerComponent = new MergeableLayerComponent(bufferedMergeableWrapper);
//        mergeableMapLayerComponent.setComponentType(ComponentType.MAP_LAYER);
//        mapEntity.addComponent(mergeableMapLayerComponent);
//
//        getEnvironment().getEntities().add(mapEntity);
    }

    @Override
    public void startEngineModule() {

    }

    @Override
    public void handleInput() {
        super.handleInput();
    }

}
