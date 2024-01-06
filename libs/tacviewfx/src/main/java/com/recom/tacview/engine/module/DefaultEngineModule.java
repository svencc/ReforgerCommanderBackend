package com.recom.tacview.engine.module;

import com.recom.tacview.engine.entitycomponentsystem.component.RenderableComponent;
import com.recom.tacview.engine.entitycomponentsystem.entity.Entity;
import com.recom.tacview.engine.entitycomponentsystem.environment.Environment;
import com.recom.tacview.engine.renderables.mergeable.ScanableNoiseMergeable;
import com.recom.tacview.engine.renderer.RenderProvider;
import com.recom.tacview.engine.units.PixelDimension;
import com.recom.tacview.service.RandomProvider;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public class DefaultEngineModule extends EngineModule {

    @NonNull
    private final RandomProvider randomProvider;

    public DefaultEngineModule(
            @NonNull final Environment environment,
            @NonNull final RandomProvider randomProvider
    ) {
        super(environment);
        this.randomProvider = randomProvider;
    }


    @Override
    public void init() {
        final Entity scanableNoiseEntity = new Entity();
        final ScanableNoiseComponent scanableNoiseComponent = new ScanableNoiseComponent(
                getEnvironment().getRendererProperties().toRendererDimension(),
                getEnvironment().getRenderProvider(),
                randomProvider
        );

        scanableNoiseEntity.addComponent(scanableNoiseComponent);
        getEnvironment().registerNewEntity(scanableNoiseEntity);
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

    @RequiredArgsConstructor
    public static class ScanableNoiseComponent extends RenderableComponent {

        @NonNull
        private final RenderProvider renderProvider;
        @NonNull
        private final ScanableNoiseMergeable scanableNoiseMergeable;

        public ScanableNoiseComponent(
                @NonNull final PixelDimension pixelDimension,
                @NonNull final RenderProvider renderProvider,
                @NonNull final RandomProvider randomProvider
        ) {
            super(pixelDimension);
            this.renderProvider = renderProvider;
            this.scanableNoiseMergeable = new ScanableNoiseMergeable(renderProvider, pixelDimension, randomProvider);
        }

        @Override
        public void prepareBuffer() {
            super.prepareBuffer();
            renderProvider.provide().renderMergeable(scanableNoiseMergeable, pixelBuffer, 0, 0);
        }

    }

}
