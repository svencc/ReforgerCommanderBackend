package com.recom.tacview.engine.module;

import com.recom.tacview.engine.entity.Entity;
import com.recom.tacview.engine.entity.component.MergeableLayerComponent;
import com.recom.tacview.engine.entity.environment.EnvironmentBase;
import com.recom.tacview.engine.graphics.ScreenComposer;
import com.recom.tacview.engine.renderables.mergeable.ScanableNoiseMergeable;
import com.recom.tacview.engine.renderer.RenderProvider;
import com.recom.tacview.property.RendererProperties;
import com.recom.tacview.service.RandomProvider;
import lombok.NonNull;

public class DefaultEngineModule extends EngineModule {

    @NonNull
    private final RendererProperties rendererProperties;
    @NonNull
    private final ScreenComposer screenComposer;
    @NonNull
    private final RandomProvider randomProvider;
    @NonNull
    private final RenderProvider renderProvider;

    public DefaultEngineModule(
            @NonNull final EnvironmentBase environment,
            @NonNull final RendererProperties rendererProperties,
            @NonNull final ScreenComposer screenComposer,
            @NonNull final RandomProvider randomProvider,
            @NonNull final RenderProvider renderProvider
    ) {
        super(environment);
        this.rendererProperties = rendererProperties;
        this.screenComposer = screenComposer;
        this.randomProvider = randomProvider;
        this.renderProvider = renderProvider;
    }


    @Override
    public void init() {
        final Entity scanableNoiseLayerEntity = new Entity();
        final MergeableLayerComponent mergeableLayerComponent = new MergeableLayerComponent(new ScanableNoiseMergeable(renderProvider, rendererProperties.toRendererDimension(), randomProvider));
        scanableNoiseLayerEntity.addComponent(mergeableLayerComponent);
        getEnvironment().getEntities().add(scanableNoiseLayerEntity);
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
