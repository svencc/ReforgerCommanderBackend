package com.recom.tacview.engine.module;

import com.recom.tacview.engine.entitycomponentsystem.entity.Entity;
import com.recom.tacview.engine.entitycomponentsystem.environment.Environment;
import com.recom.tacview.engine.renderer.RenderProvider;
import com.recom.tacview.property.RendererProperties;
import com.recom.tacview.service.RandomProvider;
import lombok.NonNull;

public class DefaultEngineModule extends EngineModule {

    @NonNull
    private final RendererProperties rendererProperties;
    @NonNull
    private final RandomProvider randomProvider;
    @NonNull
    private final RenderProvider renderProvider;

    public DefaultEngineModule(
            @NonNull final Environment environment,
            @NonNull final RendererProperties rendererProperties,
            @NonNull final RandomProvider randomProvider,
            @NonNull final RenderProvider renderProvider
    ) {
        super(environment);
        this.rendererProperties = rendererProperties;
        this.randomProvider = randomProvider;
        this.renderProvider = renderProvider;
    }


    @Override
    public void init() {
        final Entity scanableNoiseLayerEntity = new Entity();
        // @TODO refactor this ...
//        final MergeableLayerComponent mergeableLayerComponent = new MergeableLayerComponent(new ScanableNoiseMergeable(renderProvider, rendererProperties.toRendererDimension(), randomProvider));
//        scanableNoiseLayerEntity.addComponent(mergeableLayerComponent);
//        getEnvironment().getEntities().add(scanableNoiseLayerEntity);
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
